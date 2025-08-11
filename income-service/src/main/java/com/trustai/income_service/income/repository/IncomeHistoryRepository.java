package com.trustai.income_service.income.repository;

import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.entity.IncomeStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IncomeHistoryRepository extends JpaRepository<IncomeHistory, Long> {
    @Query("""
        SELECT ih.incomeType,
               SUM(CASE WHEN FUNCTION('DATE', ih.createdAt) = :today THEN ih.amount ELSE 0 END) AS dailyIncome,
               SUM(ih.amount) AS totalIncome
        FROM IncomeHistory ih
        WHERE ih.userId = :userId
        GROUP BY ih.incomeType
    """)
    List<Object[]> findIncomeSummaryByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);


    @Query(value = """
        SELECT ih.income_type AS type,
               COALESCE(SUM(ih.amount), 0) AS totalIncome,
               COALESCE(SUM(CASE WHEN ih.created_at >= CURRENT_DATE THEN ih.amount ELSE 0 END), 0) AS todayIncome,
               COALESCE(SUM(CASE WHEN ih.created_at >= CURRENT_DATE - INTERVAL '7' DAY THEN ih.amount ELSE 0 END), 0) AS last7DaysIncome
        FROM income_history ih
        WHERE ih.user_id = :userId
        GROUP BY ih.income_type
    """, nativeQuery = true)
    List<IncomeStatsProjection> findIncomeStatsByUser(@Param("userId") Long userId);
}
