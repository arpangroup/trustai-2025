package com.trustai.income_service.income.controller;

import com.trustai.income_service.income.dto.UserIncomeSummaryDto;
import com.trustai.income_service.income.service.UserIncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/income")
@RequiredArgsConstructor
public class UserIncomeController {
    private final UserIncomeService userIncomeService;

    @GetMapping("/{userId}/summary")
    public List<UserIncomeSummaryDto> getIncomeSummary(@PathVariable Long userId) {
        return userIncomeService.getUserIncomeSummary(userId);
    }
}
