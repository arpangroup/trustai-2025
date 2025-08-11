package com.trustai.investment_service.controller;

import com.trustai.investment_service.dto.InvestmentRequest;
import com.trustai.investment_service.dto.InvestmentResponse;
import com.trustai.investment_service.dto.UserInvestmentSummary;
import com.trustai.investment_service.enums.InvestmentStatus;
import com.trustai.investment_service.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/investments")
@RequiredArgsConstructor
@Slf4j
public class InvestmentController {
    private final InvestmentService investmentService;


    @PostMapping("/subscribe")
    public ResponseEntity<InvestmentResponse> subscribe(@RequestBody @Valid InvestmentRequest request) {
        log.info("Received investment subscription request: userId={}, schemaId={}, amount={}", request.getUserId(), request.getSchemaId(), request.getAmount());
        InvestmentResponse response = investmentService.subscribeToInvestment(
                request.getUserId(), request.getSchemaId(), request.getAmount());

        log.info("Subscription successful: investmentId={}, userId={}", response.investmentId(), request.getUserId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserInvestmentSummary>> getAllInvestments(
            @RequestParam(required = false) InvestmentStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(investmentService.getAllInvestments(status, pageable));
    }

    @GetMapping("/{investmentId}")
    public ResponseEntity<UserInvestmentSummary> getInvestmentDetails(@PathVariable Long investmentId) {
        return ResponseEntity.ok(investmentService.getInvestmentDetails(investmentId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<UserInvestmentSummary>> getUserInvestments(
            @PathVariable Long userId,
            @RequestParam(required = false) InvestmentStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(investmentService.getUserInvestments(userId, status, pageable));
    }


    @GetMapping("/user/{userId}/export")
    public ResponseEntity<List<UserInvestmentSummary>> exportUserInvestments(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.exportUserInvestments(userId));
    }
}
