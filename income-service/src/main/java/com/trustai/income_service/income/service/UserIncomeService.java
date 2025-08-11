package com.trustai.income_service.income.service;

import com.trustai.income_service.income.dto.UserIncomeSummaryDto;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.repository.IncomeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserIncomeService {
    private final IncomeHistoryRepository incomeRepo;

    /*public List<UserIncomeSummaryDto> getUserIncomeSummary(Long userId) {
        List<Object[]> rawResults = incomeRepo.findIncomeSummaryByUserId(userId, LocalDate.now());

        BigDecimal dailyIncomeTotal = BigDecimal.ZERO;
        BigDecimal totalIncomeTotal = BigDecimal.ZERO;

        BigDecimal dailyTeam = BigDecimal.ZERO;
        BigDecimal totalTeam = BigDecimal.ZERO;

        Map<IncomeHistory.IncomeType, UserIncomeSummaryDto> summaryMap = new EnumMap<>(IncomeHistory.IncomeType.class);
        for (Object[] row : rawResults) {
            IncomeHistory.IncomeType type = (IncomeHistory.IncomeType) row[0];
            BigDecimal dailyIncome = (BigDecimal) row[1];
            BigDecimal totalIncome = (BigDecimal) row[2];

            String label = switch (type) {
                case DAILY -> "Comprehensive";
                case TEAM -> "Team";
            };

            summaryMap.put(type, new UserIncomeSummaryDto(label, dailyIncome, totalIncome));
        }

        // Ensure all types are included even if income = 0
        return Stream.of(
                summaryMap.getOrDefault(IncomeHistory.IncomeType.DAILY, new UserIncomeSummaryDto("Comprehensive", BigDecimal.ZERO, BigDecimal.ZERO)),
                new UserIncomeSummaryDto("Reserve", BigDecimal.valueOf(1.30), BigDecimal.valueOf(1.30)), // example hardcoded value
                summaryMap.getOrDefault(IncomeHistory.IncomeType.TEAM, new UserIncomeSummaryDto("Team", BigDecimal.ZERO, BigDecimal.ZERO))
        ).toList();
    }*/

    public List<UserIncomeSummaryDto> getUserIncomeSummary(Long userId) {
        List<Object[]> rawResults = incomeRepo.findIncomeSummaryByUserId(userId, LocalDate.now());

        // Aggregate income by type
        Map<IncomeHistory.IncomeType, UserIncomeSummaryDto> summaryMap = new EnumMap<>(IncomeHistory.IncomeType.class);

        for (Object[] row : rawResults) {
            IncomeHistory.IncomeType type = (IncomeHistory.IncomeType) row[0];
            BigDecimal dailyIncome = (BigDecimal) row[1];
            BigDecimal totalIncome = (BigDecimal) row[2];

            summaryMap.merge(type,
                    new UserIncomeSummaryDto(getLabel(type), dailyIncome, totalIncome),
                    (existing, incoming) -> new UserIncomeSummaryDto(
                            existing.type(),
                            existing.dailyIncome().add(incoming.dailyIncome()),
                            existing.totalIncome().add(incoming.totalIncome())
                    )
            );
        }

        // Add hardcoded "Reserve" income in the middle
        return List.of(
                summaryMap.getOrDefault(IncomeHistory.IncomeType.DAILY, new UserIncomeSummaryDto("Comprehensive", BigDecimal.ZERO, BigDecimal.ZERO)),
                new UserIncomeSummaryDto("Reserve", new BigDecimal("1.30"), new BigDecimal("1.30")),
                summaryMap.getOrDefault(IncomeHistory.IncomeType.TEAM, new UserIncomeSummaryDto("Team", BigDecimal.ZERO, BigDecimal.ZERO))
        );
    }

    private String getLabel(IncomeHistory.IncomeType type) {
        return switch (type) {
            case DAILY -> "Comprehensive";
            case TEAM -> "Team";
            case REFERRAL -> "Referral";
            case RESERVE ->  "Reserve";
        };
    }
}
