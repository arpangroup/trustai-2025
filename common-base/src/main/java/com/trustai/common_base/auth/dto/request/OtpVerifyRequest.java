package com.trustai.common_base.auth.dto.request;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String username;
    private String otp;
    private String sessionId; // optional session token returned from start flow
}
