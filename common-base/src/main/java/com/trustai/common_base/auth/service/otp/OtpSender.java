package com.trustai.common_base.auth.service.otp;

public interface OtpSender {
    void sendOtp(String to, String otp);
}
