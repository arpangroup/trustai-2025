package com.trustai.common_base.auth.dto.response;


public record AuthResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiry,
        long refreshTokenExpiry
) {}
