package com.trustai.user_service.hierarchy.service;

import com.trustai.common_base.api.TransactionApi;
import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.dto.UserHierarchyStats;
import com.trustai.common_base.dto.UserMetrics;
import com.trustai.common_base.repository.user.UserRepository;
import com.trustai.user_service.hierarchy.UserHierarchy;
import com.trustai.user_service.hierarchy.repository.UserHierarchyRepository;
import com.trustai.user_service.user.exception.IdNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMetricsService {
    private final UserHierarchyRepository hierarchyRepo;
    private final UserRepository userRepository;
    //private final DepositService depositService;
    private final TransactionApi transactionClient;

    public UserMetrics computeMetrics(Long userId) {
        List<UserHierarchy> downlines = hierarchyRepo.findByAncestor(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IdNotFoundException("userId " + userId + " not found"));

        // Aggregate team counts by depth
        Map<Integer, Long> depthCounts = downlines.stream()
                .filter(uh -> uh.getDepth() > 0)
                .collect(Collectors.groupingBy(
                        UserHierarchy::getDepth,
                        Collectors.counting()
                ));

        long teamSize = depthCounts.values().stream().mapToLong(Long::longValue).sum();

        UserHierarchyStats stats = UserHierarchyStats.builder()
                .depthWiseCounts(depthCounts)
                .totalTeamSize(teamSize)
                .build();

        BigDecimal totalDeposit = transactionClient.getDepositBalance(userId);
        //BigDecimal totalDeposit = user.getDepositBalance();
        return UserMetrics.builder()
                .directReferrals(depthCounts.getOrDefault(1, 0L).intValue())
                .userHierarchyStats(stats)
                .totalDeposit(totalDeposit == null ? BigDecimal.ZERO : totalDeposit)
                .totalInvestment(BigDecimal.ZERO)
                .walletBalance(user.getWalletBalance() != null ? user.getWalletBalance() : BigDecimal.ZERO)
                .totalEarnings(BigDecimal.ZERO)
                .build();
    }
}
