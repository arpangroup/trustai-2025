package com.trustai.common_base.auth.registration;

import com.trustai.common_base.auth.dto.request.OtpVerifyRequest;
import com.trustai.common_base.auth.dto.response.AuthResponse;
import com.trustai.common_base.auth.service.otp.OtpSession;
import com.trustai.common_base.domain.user.User;

public interface RegistrationService {
    OtpSession createPendingRegistration(RegistrationRequest request);
    AuthResponse completeRegistration(String sessionId, String otp);
    User directRegister(User user, String referralCode);
}
