package com.trustai.user_service.controller;

import com.trustai.user_service.auth.request.LoginRequest;
import com.trustai.user_service.auth.request.RegistrationRequest;
import com.trustai.user_service.user.dto.AvailabilityResponse;
import com.trustai.user_service.user.dto.EmailRequest;
import com.trustai.user_service.user.dto.EmailVerificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

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
