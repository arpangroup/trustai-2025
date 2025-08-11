package com.trustai.common_base.auth.service;

import com.trustai.common_base.auth.dto.request.AuthRequest;
import com.trustai.common_base.auth.dto.response.AuthResponse;
import com.trustai.common_base.auth.service.otp.OtpService;
import com.trustai.common_base.auth.strategy.AuthenticationStrategy;
import com.trustai.common_base.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final Map<String, AuthenticationStrategy> strategies; // injected by Spring: bean name -> impl
    private final OtpService otpService;
    private final JwtProvider jwtProvider;

    public Object startAuth(AuthRequest req) {
        String flow = req.getFlow() == null ? "password" : req.getFlow();
        AuthenticationStrategy s = strategies.get(flow);
        if (s == null) throw new IllegalArgumentException("Unknown auth flow: " + flow);
        return s.start(req);
    }

    public AuthResponse verifyOtpAndIssueToken(String sessionId, String otp) {
        boolean ok = otpService.verify(sessionId, otp);
        if (!ok) throw new IllegalArgumentException("Invalid OTP");
        String username = otpService.getUsernameForSession(sessionId);
        String token = jwtProvider.generateToken(username);
        long expAt = System.currentTimeMillis() + 3600_000; // match config in real impl
        return new AuthResponse(token, expAt);
    }

    public AuthResponse issueTokenForUsername(String username) {
        String token = jwtProvider.generateToken(username);
        long expAt = System.currentTimeMillis() + 3600_000;
        return new AuthResponse(token, expAt);
    }
}
