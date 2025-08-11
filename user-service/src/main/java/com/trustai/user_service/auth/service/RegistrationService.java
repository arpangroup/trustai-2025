package com.trustai.user_service.auth.service;

import com.trustai.common_base.domain.user.User;
import com.trustai.user_service.auth.request.CompleteRegistrationRequest;
import com.trustai.user_service.auth.request.InitiateRegistrationRequest;
import com.trustai.user_service.auth.request.VerifyEmailRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface RegistrationService {
    void initiateRegistration(InitiateRegistrationRequest request, HttpServletRequest servletRequest);
    void requestEmailVerification(String email);
    void verifyEmail(VerifyEmailRequest request);
    void completeRegistration(CompleteRegistrationRequest request);
    User directRegister(User user, String referralCode);
}
