package com.trustai.investment_service.reservation.service.impl;

import com.trustai.common_base.api.UserApi;
import com.trustai.common_base.api.WalletApi;
import com.trustai.common_base.dto.TransactionDto;
import com.trustai.common_base.dto.UserInfo;
import com.trustai.common_base.dto.WalletUpdateRequest;
import com.trustai.common_base.enums.TransactionType;
import com.trustai.common_base.event.StakeSoldEvent;
import com.trustai.common_base.exceptions.ErrorCode;
import com.trustai.common_base.exceptions.ValidationException;
import com.trustai.common_base.utils.DateUtils;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.enums.InvestmentStatus;
import com.trustai.investment_service.repository.SchemaRepository;
import com.trustai.investment_service.reservation.dto.UserReservationDto;
import com.trustai.investment_service.reservation.entity.UserReservation;
import com.trustai.investment_service.reservation.mapper.UserReservationMapper;
import com.trustai.investment_service.reservation.repository.StakeReservationRepository;
import com.trustai.investment_service.reservation.service.StakeReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class StakeReservationServiceImpl implements StakeReservationService {
    private final StakeReservationRepository reservationRepository;
    private final SchemaRepository schemaRepository;
    private final UserReservationMapper mapper;
    private final UserApi userApi;
    private final WalletApi walletApi;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${investment.stake.valuationDelta}")
    private BigDecimal stakeValuationDelta;


    /**
     * Automatically reserves a stake for a given user based on available schemas.
     * Checks user's eligibility, wallet balance, and reserves the highest-priced schema.
     *
     * @param userId ID of the user making the reservation
     * @return Saved reservation object
     */
    @Override
    @Transactional
    public UserReservation autoReserve(Long userId) {
        log.info("Attempting to reserve - userId: {}", userId);
        UserInfo user = userApi.getUserById(userId);

        // Step 1. Check existing reservation for today
        boolean alreadyReserved = reservationRepository.existsByUserIdAndReservationDate(userId, LocalDate.now());
        if (alreadyReserved) {
            log.warn("Reservation failed: User has already reserved today - userId: {}", userId);
            throw new ValidationException("User has already reserved a stake today", ErrorCode.STAKE_ALREADY_RESERVED );
        }

        // Step 2. Get max-priced eligible stake schema
        InvestmentSchema schema = schemaRepository
                .findTopByInvestmentSubTypeAndIsActiveTrueOrderByPriceDesc(InvestmentSchema.InvestmentSubType.STAKE)
                .orElseThrow(() -> {
                    log.error("Reservation failed: No suitable stake schema found");
                    throw new ValidationException("No suitable stake schema found for reservation", ErrorCode.STAKE_SCHEMA_NOT_FOUND );
                });

        // Step 3. Validate user wallet balance
        BigDecimal walletBalance = user.getWalletBalance();
        if (walletBalance.compareTo(schema.getMinimumInvestmentAmount()) < 0) {
            log.warn("Reservation failed: Insufficient wallet balance. userId={}, balance={}, required={}",
                    userId, walletBalance, schema.getMinimumInvestmentAmount());
            throw new ValidationException("Insufficient Wallet Balance", ErrorCode.INSUFFICIENT_WALLET_BALANCE );
        }

        // Step 4. Create and save reservation
        UserReservation reservation = UserReservation.builder()
                .userId(userId)
                .schema(schema)
                .reservedAmount(schema.getPrice())
                .valuationDelta(stakeValuationDelta == null ? BigDecimal.ZERO : stakeValuationDelta)
                .reservedAt(LocalDateTime.now())
                .expiryAt(LocalDateTime.now().plusDays(1))
                .incomeEarned(BigDecimal.ZERO)
                .isSold(false)
                .build();

        // Step 5. Update Wallet Balance:
        TransactionDto walletTxn = deductWalletBalance(userId, schema.getPrice());

        UserReservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation successful - reservationId: {}, userId: {}", savedReservation.getId(), userId);

        return savedReservation;
    }


    /**
     * Deprecated reservation method with manual inputs. Not supported.
     */

    @Deprecated
    @Override
    public UserReservation reserve(Long userId, Long schemaId, BigDecimal amount) {
        return null;
    }


    /**
     * Sells an existing reservation by marking it as sold and setting the sold timestamp.
     * Future implementation can include profit calculation.
     *
     * @param reservationId ID of the reservation to sell
     * @param userId        User who owns the reservation
     */
    @Override
    public void sellReservation(Long reservationId, Long userId) {
        log.info("Attempting to sell reservation - reservationId: {}, userId: {}", reservationId, userId);

        UserReservation reservation = reservationRepository
                .findByIdAndUserIdAndIsSoldFalse(reservationId, userId)
                .orElseThrow(() -> {
                    log.warn("Sell failed - reservation not found or already sold. reservationId: {}, userId: {}", reservationId, userId);
                    throw new ValidationException("Reservation not found or already sold.", ErrorCode.RESERVATION_NOT_FOUND_OR_SOLD );
                });

        // TODO: Implement logic to calculate profit (reservedAmount * dailyIncomePercentage)
        // TODO: Implement logic to mark the reservation as sold for the specified user.
        //reservation.setIncomeEarned(); // <----Calculate income earned
        //var profit = (reservation.getReservedAmount() + reservation.getValuationDelta() ) * dailyIncomePercentage;
        reservation.setSold(true);
        reservation.setSoldAt(LocalDateTime.now());

        reservationRepository.save(reservation);

        log.info("Reservation sold successfully - reservationId: {}, userId: {}", reservationId, userId);

        // Publish event to trigger daily income
        BigDecimal saleAmount = reservation.getReservedAmount(); // or your calculation
        eventPublisher.publishEvent(new StakeSoldEvent(userId, saleAmount));
        log.info("Published StakeSoldEvent for userId: {}, saleAmount: {}", userId, saleAmount);
    }


    /**
     * Fetch all active (unsold and unexpired) reservations for a user.
     *
     * @param userId ID of the user
     * @return List of active reservation DTOs
     */
    @Override
    public List<UserReservationDto> getActiveReservations(Long userId) {
        log.info("Fetching active reservations - userId: {}", userId);
        List<UserReservation> activeReservations = reservationRepository
                .findByUserIdAndIsSoldFalseAndExpiryAtAfter(userId, LocalDateTime.now());

        List<UserReservationDto> dtos =  activeReservations.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        log.info("Found {} active reservations for userId: {}", dtos.size(), userId);
        return dtos;
    }

    /**
     * Expires all unclaimed reservations that are past their expiry time.
     * Currently marks them only; logic can be extended to refund or log investments.
     */
    @Override
    public void expireUnclaimedReservations() {
        log.info("Running expiration of unclaimed reservations...");
        List<UserReservation> expired = reservationRepository.findByIsSoldFalseAndExpiryAtBefore(LocalDateTime.now());

        if (expired.isEmpty()) {
            log.info("No unclaimed reservations to expire.");
            return;
        }

        for (UserReservation r : expired) {
            log.info("Expiring reservation id={} for user={}", r.getId(), r.getUserId());
            // You could also move to a collection or user investment if needed
        }

        reservationRepository.saveAll(expired);
        log.info("Expired {} reservations.", expired.size());
    }

    /**
     * Deducts the reserved amount from the user's wallet by creating an investment transaction.
     * Can be part of a larger transaction block if wallet supports rollback.
     *
     * @param userId        ID of the user
     * @param reservedAmount Amount to deduct from the wallet
     */
    private TransactionDto deductWalletBalance(Long userId, BigDecimal reservedAmount) {
        log.info("Deducting wallet balance - userId: {}, amount: {}", userId, reservedAmount);

        WalletUpdateRequest walletUpdateRequest = new WalletUpdateRequest(
                reservedAmount,
                TransactionType.INVESTMENT_RESERVE,
                false,
                "investment-reserved",
                "Investment reserved at " + DateUtils.formatDisplayDate(LocalDateTime.now()),
                null
        );
        TransactionDto txn = walletApi.updateWalletBalance(userId, walletUpdateRequest);
        if (txn == null || txn.getId() == null) {
            log.error("Wallet deduction failed - userId: {}, amount: {}", userId, reservedAmount);
            throw new ValidationException("Wallet deduction failed", ErrorCode.WALLET_DEDUCTION_FAILED );
        }
        log.info("Wallet deduction successful - txnId: {}, userId: {}", txn.getId(), userId);
        return txn;
    }
}
