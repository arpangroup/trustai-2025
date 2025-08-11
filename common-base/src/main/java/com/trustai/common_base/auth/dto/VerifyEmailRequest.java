package com.trustai.common_base.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerifyEmailRequest {
    public String email;
    public String otp;
}
