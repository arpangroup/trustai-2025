package com.trustai.user_service.hierarchy.repository;

public interface DownlineUserIds {
    Integer getDepth();
    String getUserIds(); // GROUP_CONCAT will return a comma-separated string
}
