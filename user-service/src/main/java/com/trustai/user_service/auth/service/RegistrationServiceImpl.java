package com.trustai.user_service.auth.service;

import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.event.UserRegisteredEvent;
import com.trustai.common_base.repository.user.UserRepository;
import com.trustai.user_service.auth.request.CompleteRegistrationRequest;
import com.trustai.user_service.auth.request.InitiateRegistrationRequest;
import com.trustai.user_service.auth.request.VerifyEmailRequest;
import com.trustai.user_service.hierarchy.service.UserHierarchyService;
import com.trustai.user_service.user.entity.Kyc;
import com.trustai.user_service.user.entity.RegistrationProgress;
import com.trustai.user_service.user.entity.VerificationToken;
import com.trustai.user_service.user.enums.VerificationType;
import com.trustai.user_service.user.exception.AuthenticationException;
import com.trustai.user_service.user.repository.RegistrationProgressRepository;
import com.trustai.user_service.user.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {
    private final RegistrationProgressRepository progressRepo;
    private final VerificationTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final UserHierarchyService userHierarchyService;
    private final RegistrationHelper registrationHelper;
    private final ApplicationEventPublisher publisher;

    // Step 1: Check username & email availability
    @Override
    public void initiateRegistration(InitiateRegistrationRequest request, HttpServletRequest servletRequest) {
        log.info("Initiate Registration: {}", request);
        if (userRepo.existsByUsername(request.username) || userRepo.existsByEmail(request.email)) {
            throw new AuthenticationException("Username or Email already in use");
        }

        log.info("Creating RegistrationProgress log.......");
        RegistrationProgress progress = progressRepo.findByEmail(request.email).orElse(new RegistrationProgress());
        progress = registrationHelper.prepareRegistrationProgress(request, progress, servletRequest);
        progress = progressRepo.save(progress);
        log.info("RegistrationProgress record created successfully");

        log.info("Enriching IP Details...");
        registrationHelper.enrichWithIpDetails(progress, servletRequest);
        progressRepo.save(progress);

        // Send OTP logic here (save to VerificationToken table)
        requestEmailVerification(request.getEmail());
    }

    // Step 2: Request for verificationCode
    @Override
    public void requestEmailVerification(String email) {
        log.info("Request Email Verification for email: {}", email);
        if (!registrationHelper.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address");
        }

        log.info("Generating verificationCode for email: {}", email);
        VerificationToken token = registrationHelper.prepareVerificationCode(email);
        tokenRepo.save(token);
        log.info("verification token for email: {} saved successfully", email);

        // Send email with code
        String subject = "Your Email Verification Code";
        String message = "Your verification code is: " + token.getCode();
        log.info("trigger email verification notification to email: {}, verificationCode: {}", email, token.getCode());
        //publishEvent(SEND_EMAIL_VERIFICATION);
        //emailService.sendEmail(email, subject, message);
    }

    // Step 3: Verify Email with Code
    @Override
    public void verifyEmail(VerifyEmailRequest request) {
        log.info("verifyEmail for email: {}, otp: {}", request.getEmail(), request.getOtp());
        RegistrationProgress progress = progressRepo.findByEmail(request.email).orElseThrow(() -> new AuthenticationException("No registration in progress"));

        log.info("Verify token.....");
        VerificationToken token = tokenRepo.findByTypeAndTarget(VerificationType.EMAIL, request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
        if (token.isVerified()) throw new IllegalArgumentException("Email already verified");
        if (!token.getCode().equals(request.otp)) throw new IllegalArgumentException("Invalid verification code");
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Verification code expired");

        log.info("token verified for email: {}", request.getEmail());
        token.setVerified(true);
        tokenRepo.save(token);

        progress.setEmailVerified(true);
        progress.setLastUpdated(LocalDateTime.now());
        log.info("update registration progress as verified for email: {}", request.getEmail());
        progressRepo.save(progress);
    }

    // Step 4: Finish the registration process
    @Override
    public void completeRegistration(CompleteRegistrationRequest request) {
        log.info("Complete Registration Request: {}", request);
        if (!request.getPassword().equals(request.confirmPassword)) throw new IllegalArgumentException("Passwords do not match");
        RegistrationProgress progress = progressRepo.findByEmail(request.email).orElseThrow(() -> new AuthenticationException("No registration in progress"));
        if (!progress.isEmailVerified())throw new AuthenticationException("Email not verified");

        // Create a new user
        User newUser = registrationHelper.mapToUser(progress, request.getPassword());
        newUser = doRegister(newUser, request.getReferralCode());
        log.info("user created successfully with userId: {}", newUser.getId());

        progress.setRegistrationCompleted(true);
        progressRepo.save(progress);
        //tokenRepo.delete(token); // Optionally delete after successful registration
    }

    private User doRegister(User user, String referralCode) {
        log.info("doRegister.....");
        userRepo.findByReferralCode(referralCode).ifPresent(user::setReferrer);
        User newUser = userRepo.save(user);

        if (newUser.getReferrer() != null) {
            log.info("Updating user hierarchy table....");
            userHierarchyService.updateHierarchy(newUser.getReferrer().getId(), newUser.getId());
        }

        log.info("publishing UserRegisteredEvent.....");
        publisher.publishEvent(new UserRegisteredEvent(newUser.getId(), newUser.getReferrer() != null ? newUser.getReferrer().getId(): null));
        return newUser;
    }

    @Override
    public User directRegister(User user, String referralCode) {
        log.info("directRegister...........");
        userRepo.findByReferralCode(referralCode).ifPresent(user::setReferrer);
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            user.setEmail(user.getUsername() + "@trustai.com");
        }
        Kyc kyc = new Kyc();
        kyc.setEmail(user.getEmail());
        kyc.setPhone(user.getMobile());
        kyc.setFirstname(user.getUsername());
        //user.setKycInfo(kyc);

        return doRegister(user, referralCode);
    }
}
