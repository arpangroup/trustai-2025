package com.trustai.income_service.income.controller;

import com.trustai.common_base.controller.BaseController;
import com.trustai.common_base.enums.IncomeType;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.entity.IncomeSummaryProjection;
import com.trustai.income_service.income.service.IncomeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
public class IncomeHistoryController extends BaseController {
    private final IncomeHistoryService incomeHistoryService;

    // 1. Summary
    @GetMapping("/summary")
    public ResponseEntity<List<IncomeSummaryProjection>> getIncomeSummary() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(incomeHistoryService.getIncomeSummary(userId));
    }

    // 2. Details with filter
    @GetMapping("/details")
    public ResponseEntity<Page<IncomeHistory>> getIncomeDetails(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) IncomeType incomeType,
            Pageable pageable
    ) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(incomeHistoryService.getIncomeDetails(userId, startDate, endDate, incomeType, pageable));
    }

}
