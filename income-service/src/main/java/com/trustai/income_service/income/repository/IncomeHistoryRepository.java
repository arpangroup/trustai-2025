package com.trustai.income_service.income.repository;

import com.trustai.common_base.enums.IncomeType;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.entity.IncomeSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public interface IncomeHistoryRepository extends JpaRepository<IncomeHistory, Long> {
    /*@Query("""
        SELECT ih.incomeType,
               SUM(CASE WHEN FUNCTION('DATE', ih.createdAt) = :today THEN ih.amount ELSE 0 END) AS dailyIncome,
               SUM(ih.amount) AS totalIncome
        FROM IncomeHistory ih
        WHERE ih.userId = :userId
        GROUP BY ih.incomeType
    """)
    List<Object[]> findIncomeSummaryByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);*/


    /*@Query(value = """
        SELECT ih.income_type AS type,
               COALESCE(SUM(ih.amount), 0) AS totalIncome,
               COALESCE(SUM(CASE WHEN ih.created_at >= CURRENT_DATE THEN ih.amount ELSE 0 END), 0) AS todayIncome,
               COALESCE(SUM(CASE WHEN ih.created_at >= CURRENT_DATE - INTERVAL '7' DAY THEN ih.amount ELSE 0 END), 0) AS last7DaysIncome
        FROM income_history ih
        WHERE ih.user_id = :userId
        GROUP BY ih.income_type
    """, nativeQuery = true)
    List<IncomeStatsProjection> findIncomeStatsByUser(@Param("userId") Long userId);*/


    // Generic summary query (optionally filtered by user)
    @Query(value = """
        SELECT ih.income_type AS incomeType,
               SUM(CASE WHEN DATE(ih.created_at) = CURRENT_DATE THEN ih.amount ELSE 0 END) AS todayAmount,
               SUM(CASE WHEN DATE(ih.created_at) = CURRENT_DATE - INTERVAL 1 DAY THEN ih.amount ELSE 0 END) AS yesterdayAmount,
               SUM(CASE WHEN DATE(ih.created_at) >= CURRENT_DATE - INTERVAL 7 DAY THEN ih.amount ELSE 0 END) AS last7DaysAmount,
               SUM(ih.amount) AS totalAmount
        FROM income_history ih
        WHERE (:userId IS NULL OR ih.user_id = :userId)
        GROUP BY ih.income_type
        """, nativeQuery = true)
    List<IncomeSummaryProjection> getIncomeSummary(@Nullable @Param("userId") Long userId);


    // Generic filter query (optionally filtered by user)
    @Query("""
        SELECT ih
        FROM IncomeHistory ih
        WHERE (:userId IS NULL OR ih.userId = :userId)
          AND (:startDate IS NULL OR ih.createdAt >= :startDate)
          AND (:endDate IS NULL OR ih.createdAt <= :endDate)
          AND (:incomeType IS NULL OR ih.incomeType = :incomeType)
    """)
    Page<IncomeHistory> findByFilters(
            @Nullable @Param("userId") Long userId,
            @Nullable @Param("startDate") LocalDateTime startDate,
            @Nullable @Param("endDate") LocalDateTime endDate,
            @Nullable @Param("incomeType") IncomeType incomeType,
            Pageable pageable
    );
}
