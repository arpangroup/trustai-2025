package com.trustai.user_service.user.validation;

import com.trustai.common_base.repository.user.UserRepository;
import com.trustai.user_service.auth.request.RegistrationRequest;
import com.trustai.user_service.user.exception.DuplicateRecordException;
import com.trustai.user_service.user.exception.InvalidRequestException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class RegistrationRequestValidatorTemplate {
    @Autowired
    protected UserRepository userRepository;

    public final void validateRegistrationRequest(RegistrationRequest request) throws InvalidRequestException {
        log.info("Validating RegistrationRequest.........");
        //verifyCaptcha();
        validateDuplicateUsername(request.getUsername());
        if (request.getEmail() != null) {
            validateValidEmailFormat(request.getEmail());
            validateDuplicateEmail(request.getEmail());
        }
        if (request.getMobile() != null) {
            validateMobileFormat(request.getMobile());
            validateDuplicateMobile(request.getMobile());
            isMobileNumberVerified(request.getMobile());
        }
        if (request.getReferralCode() != null){
            validateReferralCode(request.getReferralCode());
        }
    }

    // Abstract methods to be implemented by subclasses
    protected abstract void validateDuplicateUsername(String username) throws DuplicateRecordException;
    protected abstract void validateValidEmailFormat(String email) throws DuplicateRecordException;
    protected abstract void validateDuplicateEmail(String email) throws DuplicateRecordException;
    protected abstract void validateMobileFormat(String mobile) throws DuplicateRecordException;
    protected abstract void validateDuplicateMobile(String mobile) throws DuplicateRecordException;
    protected abstract boolean isMobileNumberVerified(String mobile) throws ValidationException;
    protected abstract void validateReferralCode(String referralCode) throws DuplicateRecordException;
}
