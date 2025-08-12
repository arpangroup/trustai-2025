package com.trustai.common_base.auth.service.otp;

import java.util.Optional;

public interface OtpService {
    OtpSession createSession(String username, String flow, int maxAttempts);
    void sendOtp(OtpSession session, String channel);
    boolean verifyOtp(String sessionId, String otp);
    void incrementAttempts(String sessionId, int maxAllowed);
    Optional<OtpSession> getSession(String sessionId);
    void invalidateSession(String sessionId);
}
