package com.trustai.common_base.auth.strategy;

import com.trustai.common_base.auth.dto.request.AuthRequest;
import com.trustai.common_base.auth.service.otp.OtpService;
import com.trustai.common_base.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("2step")
@RequiredArgsConstructor
public class TwoStepOtpAuthStrategy implements AuthenticationStrategy {
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Object start(AuthRequest request) {
        // 1) validate username/password first (or you may skip password verification for passwordless OTP)
        var token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(token);

        // 2) generate and send OTP to user's preferred destination (email or mobile)
        var user = (com.trustai.trustai_common.security.service.CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsername());
        String destination = user.getEmail(); // or mobile if prefer mobile
        String sessionId = otpService.generateAndSend(request.getUsername(), destination);
        return sessionId;
    }
}
