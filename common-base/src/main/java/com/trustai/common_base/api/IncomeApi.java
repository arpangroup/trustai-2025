package com.trustai.common_base.api;

import com.trustai.common_base.dto.IncomeSummaryDto;
import com.trustai.common_base.dto.TransactionDto;
import com.trustai.common_base.dto.WalletUpdateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;


public interface IncomeApi {
    IncomeSummaryDto getIncomeSummary(Long userId);
}
