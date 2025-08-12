package com.trustai.common_base.auth.service;

import com.trustai.common_base.auth.dto.request.AuthRequest;
import com.trustai.common_base.auth.dto.response.AuthResponse;
import com.trustai.common_base.auth.exception.BadCredentialsException;
import com.trustai.common_base.auth.exception.UnsupportedAuthFlowException;
import com.trustai.common_base.auth.service.otp.OtpService;
import com.trustai.common_base.auth.service.otp.OtpSession;
import com.trustai.common_base.auth.strategy.AuthenticationStrategy;
import com.trustai.common_base.constants.SecurityConstants;
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

    /**
     * Step 1: Start authentication flow (password, passwordless, etc.)
     */
    public Object startAuth(AuthRequest req) {
        String flow = req.flow() == null ? "password" : req.flow();
        AuthenticationStrategy strategy  = strategies.get(flow);
        if (strategy  == null) throw new UnsupportedAuthFlowException(flow);
        return strategy .start(req);
    }

    /**
     * Step 2: Verify OTP and issue JWT token (for passwordless or OTP login)
     */
    public AuthResponse verifyOtpAndIssueToken(String sessionId, String otp) {
        if (StringUtils.isBlank(sessionId)) throw new BadCredentialsException("Invalid sessionId");
        OtpSession session = otpService.getSession(sessionId).orElseThrow(() -> new BadCredentialsException("Invalid or expired OTP session"));

        otpService.incrementAttempts(sessionId, 3); // max 3 attempts
        boolean ok = otpService.verifyOtp(sessionId, otp);
        if (!ok) throw new BadCredentialsException("Invalid OTP");

        String token = jwtProvider.generateToken(session.username());
        otpService.invalidateSession(sessionId);

        long expAt = System.currentTimeMillis() + SecurityConstants.JWT_EXPIRE_MILLS; // 1 hour expiry
        return new AuthResponse(token, expAt);
    }

    /**
     * Issue token without OTP (used in password login)
     */
    public AuthResponse issueTokenForUsername(String username) {
        String token = jwtProvider.generateToken(username);
        long expAt = System.currentTimeMillis() + SecurityConstants.JWT_EXPIRE_MILLS;
        return new AuthResponse(token, expAt);
    }
}
