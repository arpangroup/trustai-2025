package com.trustai.income_service.income.controller;

import com.trustai.common_base.controller.BaseController;
import com.trustai.income_service.income.dto.UserIncomeSummary;
import com.trustai.income_service.income.service.UserIncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/income")
@RequiredArgsConstructor
public class UserIncomeController extends BaseController {
    private final UserIncomeService userIncomeService;

    @GetMapping("/summary")
    public List<UserIncomeSummary> getIncomeSummary() {
        Long userId = getCurrentUserId();
        return userIncomeService.getUserIncomeSummary(userId);
    }
}
