package com.trustai.investment_service.reservation.service;

import com.trustai.investment_service.reservation.dto.UserReservationDto;
import com.trustai.investment_service.reservation.entity.UserReservation;

import java.math.BigDecimal;
import java.util.List;

public interface StakeReservationService {
    /**
     * Aut-Reserve a stake for the user. Only one reservation per day is allowed.
     *
     * @param userId   the user ID
     * @return created UserReservation
     */
    UserReservation autoReserve(Long userId);

    /**
     * Reserve a schema for the user. Only one reservation per day is allowed.
     * @param userId
     * @param schemaId
     * @param amount
     * @return
     */
    @Deprecated
    UserReservation reserve(Long userId, Long schemaId, BigDecimal amount);

    /**
     * Sell a previously reserved stake/schema.
     *
     * @param reservationId reservation ID
     * @param userId        user ID (to verify ownership)
     */
    void sellReservation(Long reservationId, Long userId);

    /**
     * Fetch all currently active (not expired or sold) reservations for the user.
     *
     * @param userId user ID
     * @return list of active reservations
     */
    List<UserReservationDto> getActiveReservations(Long userId);

    /**
     * Expire all reservations that have passed their expiry time and are still unsold.
     */
    void expireUnclaimedReservations();
}
