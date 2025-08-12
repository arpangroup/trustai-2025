package com.trustai.common_base.auth.strategy;

import com.trustai.common_base.auth.dto.TokenPair;
import com.trustai.common_base.auth.dto.request.AuthRequest;
import com.trustai.common_base.auth.dto.response.AuthResponse;
import com.trustai.common_base.auth.exception.BadCredentialsException;
import com.trustai.common_base.auth.service.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Object start(AuthRequest request) {
        try {
            // Authenticate using username & password
            var authToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());
            var auth = authenticationManager.authenticate(authToken); // throws if invalid
        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Generate token pair
        TokenPair tokens = jwtProvider.generateToken(request.username());

        // Store refresh token with expiry
        refreshTokenService.storeToken(request.username(), tokens.refreshToken(), tokens.refreshTokenExpiry());

        // Return complete AuthResponse (access + refresh + expiry)
        return new AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.accessTokenExpiry(),
                tokens.refreshTokenExpiry()
        );
    }
}
