package com.trustai.investment_service.reservation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ReservationSummary {
    private BigDecimal todayEarning;
    private BigDecimal cumulativeIncome;
    private BigDecimal todayTeamIncome;
    private BigDecimal totalTeamIncome;
    private ReservationRange reservationRange;
    private int reservedCount;
    private BigDecimal walletBalance;

    public record ReservationRange(BigDecimal startPrice, BigDecimal endPrice){};
}
