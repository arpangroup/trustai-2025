package com.trustai.user_service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailVerificationRequest {
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^$|^.+$", message = "Email must not be only whitespace")
    private String email;

    private String verificationCode;
}
