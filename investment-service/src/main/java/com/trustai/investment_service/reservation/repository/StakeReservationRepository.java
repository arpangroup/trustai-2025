package com.trustai.investment_service.reservation.repository;

import com.trustai.investment_service.reservation.entity.UserReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StakeReservationRepository extends JpaRepository<UserReservation, Long> {
    boolean existsByUserIdAndReservationDate(Long userId, LocalDate reservationDate);

    List<UserReservation> findByUserIdAndIsSoldFalseAndExpiryAtAfter(Long userId, LocalDateTime now);

    List<UserReservation> findByIsSoldFalseAndExpiryAtBefore(LocalDateTime now);

    Optional<UserReservation> findByIdAndUserIdAndIsSoldFalse(Long reservationId, Long userId);
}
