package com.trustai.common_base.auth.dto.request;

public record AuthRequest(String username, String password, String flow) {}