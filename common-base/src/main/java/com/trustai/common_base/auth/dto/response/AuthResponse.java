package com.trustai.common_base.auth.dto.response;


public record AuthResponse(String token, long expiresAt) {}
