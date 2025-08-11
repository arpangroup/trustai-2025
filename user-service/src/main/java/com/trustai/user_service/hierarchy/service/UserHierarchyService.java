package com.trustai.user_service.hierarchy.service;

import com.trustai.user_service.hierarchy.UserHierarchy;
import com.trustai.user_service.hierarchy.UserTreeNode;

import java.util.List;
import java.util.Map;

public interface UserHierarchyService {
    void updateHierarchy(Long referredId, Long newUserId);
    UserTreeNode getDownlineTree(Long rootUserId, int maxLevel);
    Map<Integer, List<Long>> getDownlinesGroupedByLevel(Long userId);
    List<UserHierarchy> findByDescendant(Long descendant);
}
