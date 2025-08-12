package com.trustai.common_base.auth.service.otp;

import com.trustai.common_base.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class InMemoryOtpService implements OtpService {

    private final SecureRandom random = new SecureRandom();

    // sessionId -> SessionData
    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();
    // sessionId -> attempts count
    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();

    @Override
    public OtpSession createSession(String username, String flow, int maxAttempts) {
        String sessionId = UUID.randomUUID().toString();
        String otp = generateOtp();

        SessionData data = new SessionData(username, otp, flow, Instant.now().toEpochMilli());
        sessions.put(sessionId, data);
        attempts.put(sessionId, 0);

        return new OtpSession(sessionId, username, flow);
    }

    @Override
    public void sendOtp(OtpSession session, String channel) {
        SessionData data = sessions.get(session.sessionId());
        if (data == null) throw new IllegalStateException("OTP session not found");

        // In real-world: integrate with Email/SMS provider here
        System.out.printf("Sending OTP %s to %s via %s%n", data.otp(), session.username(), channel);
    }

    @Override
    public boolean verifyOtp(String sessionId, String otp) {
        SessionData data = sessions.get(sessionId);
        if (data == null) return false;
        if (isExpired(data)) {
            invalidateSession(sessionId);
            return false;
        }
        return data.otp().equals(otp);
    }

    @Override
    public void incrementAttempts(String sessionId, int maxAllowed) {
        int current = attempts.getOrDefault(sessionId, 0) + 1;
        attempts.put(sessionId, current);

        if (current > maxAllowed) {
            invalidateSession(sessionId);
            throw new RuntimeException("Too many OTP attempts");
        }
    }

    @Override
    public Optional<OtpSession> getSession(String sessionId) {
        SessionData data = sessions.get(sessionId);
        if (data == null || isExpired(data)) {
            invalidateSession(sessionId);
            return Optional.empty();
        }
        return Optional.of(new OtpSession(sessionId, data.username(), data.flow()));
    }

    @Override
    public void invalidateSession(String sessionId) {
        sessions.remove(sessionId);
        attempts.remove(sessionId);
    }

    private boolean isExpired(SessionData data) {
        return Instant.now().toEpochMilli() - data.createdAt() > SecurityConstants.OTP_TTL_MILLIS;
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, SecurityConstants.OTP_LENGTH);
        int code = random.nextInt(bound);
        return String.format("%0" + SecurityConstants.OTP_LENGTH + "d", code);
    }

    private record SessionData(String username, String otp, String flow, long createdAt) { }
}
