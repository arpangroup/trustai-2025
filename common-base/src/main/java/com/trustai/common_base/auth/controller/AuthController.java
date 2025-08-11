package com.trustai.common_base.auth.controller;

import com.trustai.common_base.auth.dto.*;
import com.trustai.common_base.auth.dto.request.AuthRequest;
import com.trustai.common_base.auth.dto.request.OtpVerifyRequest;
import com.trustai.common_base.auth.dto.response.AuthResponse;
import com.trustai.common_base.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    /**
     * Start authentication. Returns either:
     * - For password flow: returns username (or directly token in other design) — here we return token for simplicity.
     * - For 2-step: returns sessionId which client must use with /verify-otp.
     */
    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody AuthRequest request) {
        Object res = authService.startAuth(request);
        if (res instanceof String) {
            // For password flow our UsernamePasswordAuthStrategy returns username; issue token immediately
            if ("password".equals(request.getFlow()) || request.getFlow() == null) {
                return ResponseEntity.ok(authService.issueTokenForUsername((String) res));
            } else {
                // 2-step returns sessionId
                return ResponseEntity.ok(Map.of("sessionId", res));
            }
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpVerifyRequest req) {
        var resp = authService.verifyOtpAndIssueToken(req.getSessionId(), req.getOtp());
        return ResponseEntity.ok(resp);
    }



    //  Rate Limiting (Mandatory)
    // GET /api/auth/availability?username=arpan&email=arpan@example.com
    //@RateLimiter(name = "checkAvailability")
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> availability(
            @RequestParam("username") String username,
            @RequestParam("email") String email) {
        return ResponseEntity.ok(new AvailabilityResponse());

    }

    @PostMapping("/request-email-verification")
    public void requestEmailVerification(@RequestBody EmailRequest request) {

    }

    @PostMapping("/verify-email")
    public void verifyEmail(@RequestBody EmailVerificationRequest request) {

    }

    @PostMapping("/register")
    public void register(@RequestBody RegistrationRequest request) {

    }

    @PostMapping("/login")
    public void login(@RequestBody LoginRequest request) {

    }
}
