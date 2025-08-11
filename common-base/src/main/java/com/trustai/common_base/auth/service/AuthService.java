package com.trustai.common_base.auth.service;

import com.trustai.common_base.auth.dto.request.AuthRequest;
import com.trustai.common_base.auth.dto.response.AuthResponse;
import com.trustai.common_base.auth.exception.BadCredentialsException;
import com.trustai.common_base.auth.exception.UnsupportedAuthFlowException;
import com.trustai.common_base.auth.service.otp.OtpService;
import com.trustai.common_base.auth.strategy.AuthenticationStrategy;
import com.trustai.common_base.security.jwt.JwtProvider;
import com.trustai.common_base.utils.StringUtils;
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
        if (s == null) throw new UnsupportedAuthFlowException(flow);
        return s.start(req);
    }

    public AuthResponse verifyOtpAndIssueToken(String sessionId, String otp) {
        if (StringUtils.isBlank(sessionId)) throw new BadCredentialsException("Invalid sessionId");
        boolean ok = otpService.verify(sessionId, otp);
        if (!ok) throw new BadCredentialsException("Invalid OTP");
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
