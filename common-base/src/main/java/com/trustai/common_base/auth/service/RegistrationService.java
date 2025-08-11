package com.trustai.common_base.auth.service;

import com.trustai.common_base.auth.dto.VerifyEmailRequest;
import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.auth.dto.CompleteRegistrationRequest;
import com.trustai.common_base.auth.dto.InitiateRegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface RegistrationService {
    void initiateRegistration(InitiateRegistrationRequest request, HttpServletRequest servletRequest);
    void requestEmailVerification(String email);
    void verifyEmail(VerifyEmailRequest request);
    void completeRegistration(CompleteRegistrationRequest request);
    User directRegister(User user, String referralCode);
}
