package com.trustai.common_base.dto;


import com.trustai.common_base.enums.TransactionType;

import java.math.BigDecimal;

public record WalletUpdateRequest (
        BigDecimal amount,
        TransactionType transactionType,
        boolean isCredit,
        String sourceModule,
        String remarks,
        String metaInfo
) {}