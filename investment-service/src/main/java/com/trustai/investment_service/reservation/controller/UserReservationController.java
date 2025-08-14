package com.trustai.investment_service.reservation.controller;

import com.trustai.common_base.controller.BaseController;
import com.trustai.investment_service.reservation.dto.ReservationRequest;
import com.trustai.investment_service.reservation.dto.ReservationSummary;
import com.trustai.investment_service.reservation.dto.UserReservationDto;
import com.trustai.investment_service.reservation.entity.UserReservation;
import com.trustai.investment_service.reservation.service.StakeReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class UserReservationController extends BaseController {
    private final StakeReservationService reservationService;

    /**
     * Get active (visible) reservations of the user.
     */
    @GetMapping
    public ResponseEntity<List<UserReservationDto>> getActiveReservations() {
        Long userId = getCurrentUserId();
        log.info("Fetching active reservations for userId: {}", userId);
        List<UserReservationDto> reservations = reservationService.getActiveReservations(userId);
        log.info("Retrieved {} active reservations for userId: {}", reservations.size(), userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Reserve a stake/schema for today.
     */
    @PostMapping("/reserve")
    public ResponseEntity<UserReservation> autoReserve(@RequestBody @Valid ReservationRequest request) {
        Long userId = getCurrentUserId();
        log.info("Received reserve request - userId: {}, schemaId: {}, amount: {}", userId, request.getSchemaId(), request.getAmount());
        UserReservation reservation = reservationService.autoReserve(userId);
        log.info("Reservation successful - reservationId: {}", reservation.getId());
        return ResponseEntity.ok(reservation);
    }

    /**
     * Sell a reservation.
     */
    @PostMapping("/{reservationId}/sell")
    public ResponseEntity<Void> sellReservation(@PathVariable Long reservationId) {
        Long userId = getCurrentUserId();
        log.info("Received sell request - reservationId: {}, userId: {}", reservationId, userId);
        reservationService.sellReservation(reservationId, userId);
        log.info("Sell successful - reservationId: {}, userId: {}", reservationId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<ReservationSummary> reservationSummary() {
        Long userId = getCurrentUserId();
        log.info("Fetching reservation info for userId: {}", userId);
        ReservationSummary reservationSummary = reservationService.getReservationSummary(userId);
        log.info("Retrieved reservation summary for userId: {}", userId);
        return ResponseEntity.ok(reservationSummary);
    }


}
