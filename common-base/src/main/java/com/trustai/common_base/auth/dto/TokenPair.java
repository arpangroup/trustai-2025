package com.trustai.common_base.auth.dto;

public record TokenPair(
        String accessToken,
        String refreshToken,
        long accessTokenExpiry,
        long refreshTokenExpiry
) {}
