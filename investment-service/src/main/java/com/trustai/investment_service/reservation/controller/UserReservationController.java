package com.trustai.investment_service.reservation.controller;

import com.trustai.investment_service.reservation.dto.ReservationRequest;
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
public class UserReservationController {
    private final StakeReservationService reservationService;

    /**
     * Reserve a stake/schema for today.
     */
    @PostMapping("/reserve")
    public ResponseEntity<UserReservation> autoReserve(@RequestBody @Valid ReservationRequest request) {
        log.info("Received reserve request - userId: {}, schemaId: {}, amount: {}", request.getUserId(), request.getSchemaId(), request.getAmount());
        UserReservation reservation = reservationService.autoReserve(request.getUserId());
        log.info("Reservation successful - reservationId: {}", reservation.getId());
        return ResponseEntity.ok(reservation);
    }

    /**
     * Sell a reservation.
     */
    @PostMapping("/{reservationId}/sell")
    public ResponseEntity<Void> sellReservation(
            @PathVariable Long reservationId,
            @RequestParam Long userId
    ) {
        log.info("Received sell request - reservationId: {}, userId: {}", reservationId, userId);
        reservationService.sellReservation(reservationId, userId);
        log.info("Sell successful - reservationId: {}, userId: {}", reservationId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get active (visible) reservations of the user.
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserReservationDto>> getActiveReservations(@RequestParam Long userId) {
        log.info("Fetching active reservations for userId: {}", userId);
        List<UserReservationDto> reservations = reservationService.getActiveReservations(userId);
        log.info("Retrieved {} active reservations for userId: {}", reservations.size(), userId);
        return ResponseEntity.ok(reservations);
    }

}
