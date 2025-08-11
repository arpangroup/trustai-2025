package com.trustai.user_service.hierarchy.repository;

public interface UplineProjection {
    int getDepth();
    Long getAncestor();
}
