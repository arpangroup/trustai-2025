package com.trustai.common_base.auth.strategy;

import com.trustai.common_base.auth.dto.request.AuthRequest;
import com.trustai.common_base.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("password")
@RequiredArgsConstructor
public class UsernamePasswordAuthStrategy implements AuthenticationStrategy {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Object start(AuthRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        var auth = authenticationManager.authenticate(token);
        // Authentication succeeded — return username (caller will use it to issue JWT)
        return auth.getName();
    }
}
