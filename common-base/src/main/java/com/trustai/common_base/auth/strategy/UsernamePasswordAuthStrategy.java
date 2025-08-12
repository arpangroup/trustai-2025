package com.trustai.common_base.auth.strategy;

import com.trustai.common_base.auth.dto.request.AuthRequest;
import com.trustai.common_base.auth.dto.response.AuthResponse;
import com.trustai.common_base.security.jwt.JwtProvider;
import com.trustai.common_base.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("password")
@RequiredArgsConstructor
@Slf4j
public class UsernamePasswordAuthStrategy implements AuthenticationStrategy {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Object start(AuthRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        var auth = authenticationManager.authenticate(token);

        // Authentication succeeded — return username (caller will use it to issue JWT)
        String jwtToken = jwtProvider.generateToken(auth.getName());
        long expAt = System.currentTimeMillis() + 3600_000; // 1 hour

        return new AuthResponse(jwtToken, expAt);
    }
}
