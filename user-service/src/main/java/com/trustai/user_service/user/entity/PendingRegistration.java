package com.trustai.user_service.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class PendingRegistration {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String passwordHash;
    private String email;
    private String emailVerificationCode;
    private String mobile;
    private String referralCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
