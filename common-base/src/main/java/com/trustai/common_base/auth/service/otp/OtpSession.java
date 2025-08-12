package com.trustai.common_base.auth.service.otp;

public record OtpSession(String sessionId, String username, String flow) { }