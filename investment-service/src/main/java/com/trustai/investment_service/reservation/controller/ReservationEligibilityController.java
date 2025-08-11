package com.trustai.investment_service.reservation.controller;

import com.trustai.investment_service.dto.EligibleInvestmentSummary;
import com.trustai.investment_service.reservation.service.ReservationEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations/eligibility")
@RequiredArgsConstructor
public class ReservationEligibilityController {
    private final ReservationEligibilityService mappingService;

    @GetMapping
    public ResponseEntity<List<EligibleInvestmentSummary>> getEligibleInvestmentSummaries(@RequestParam("userId") Long userId) {
        List<EligibleInvestmentSummary> result = mappingService.getEligibleInvestmentSummaries(userId);
        return ResponseEntity.ok(result);
    }
}
