package com.trustai.common_base.auth.registration;

import com.trustai.common_base.auth.dto.request.OtpVerifyRequest;
import com.trustai.common_base.auth.dto.response.AuthResponse;
import com.trustai.common_base.auth.entity.RegistrationProgress;
import com.trustai.common_base.auth.entity.VerificationToken;
import com.trustai.common_base.auth.entity.VerificationType;
import com.trustai.common_base.auth.exception.AuthException;
import com.trustai.common_base.auth.exception.BadCredentialsException;
import com.trustai.common_base.auth.service.AuthService;
import com.trustai.common_base.auth.service.otp.OtpService;
import com.trustai.common_base.auth.service.otp.OtpSession;
import com.trustai.common_base.constants.SecurityConstants;
import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.event.UserRegisteredEvent;
import com.trustai.common_base.repository.user.UserRepository;
import com.trustai.common_base.utils.IdConverter;
import com.trustai.common_base.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {
    private final PendingUserRepository pendingRepo;
    private final UserRepository userRepo;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final ApplicationEventPublisher publisher;

    private static final String REG_FLOW = "REGISTER";
    private static final int MAX_REFERRAL_CODE_LENGTH = 8;

    @Override
    public OtpSession createPendingRegistration(RegistrationRequest request) {
        log.info("Start pending registration for email: {}", request.getEmail());

        // Validate input
        if (StringUtils.isBlank(request.getUsername())) {
            log.warn("Validation failed: username is blank");
            throw new BadCredentialsException("Invalid username");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            log.warn("Validation failed: password is blank");
            throw new BadCredentialsException("Invalid password");
        }
        if (StringUtils.isBlank(request.getEmail())) {
            log.warn("Validation failed: email is blank");
            throw new BadCredentialsException("Invalid email");
        }
        if (StringUtils.isBlank(request.getReferralCode())) {
            log.warn("Validation failed: referralCode is blank");
            throw new BadCredentialsException("Invalid referralCode");
        }

        // Step 1: Check uniqueness
        if(userRepo.existsByUsername(request.getUsername()) || pendingRepo.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }
        if(userRepo.existsByEmail(request.getEmail()) || pendingRepo.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Step2: Verify ReferralCode:
        if (userRepo.existsByReferralCode(request.getReferralCode())) {
            log.warn("Invalid referral code: {}", request.getReferralCode());
            throw new IllegalArgumentException("referralCode is invalid");
        }

        // Step 3: Create PendingUser
        log.info("Creating pending user for username: {}", request.getUsername());
        PendingUser pending = new PendingUser();
        pending.setUsername(request.getUsername());
        pending.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        pending.setEmail(request.getEmail());
        pending.setMobile(request.getMobile());
        pending.setReferralCode(request.getReferralCode());

        pendingRepo.save(pending);
        log.info("Pending user saved successfully: {}", request.getUsername());

        // Step 4. Create OTP Session
        OtpSession otpSession = otpService.createSession(request.getUsername(), REG_FLOW, SecurityConstants.MAX_OTP_ATTEMPTS);
        otpService.sendOtp(otpSession, "EMAIL"); // or SMS, depending on channel


        log.info("Enriching IP Details...");
        //registrationHelper.enrichWithIpDetails(progress, servletRequest);

        //emailService.sendVerificationEmail(request.getEmail(), token);

        log.info("OTP session created and sent for username: {}", request.getUsername());
        return otpSession;
    }

    @Transactional
    public AuthResponse completeRegistration(String sessionId, String otp) {
        log.info("Completing registration for sessionId: {}", sessionId);

        OtpSession session = otpService.getSession(sessionId)
                .orElseThrow(() -> {
                    log.warn("Invalid or expired OTP session: {}", sessionId);
                    return new BadCredentialsException("Invalid or expired OTP session");
                });

        PendingUser pendingUser = pendingRepo.findByUsername(session.username())
                .orElseThrow(() -> {
                    log.error("Pending user not found for username: {}", session.username());
                    return new RuntimeException("username not found in pending user");
                });

        // Move to permanent users table
        log.info("Mapping pending user to permanent user: {}", pendingUser.getUsername());
        User newUser = mapFromPending(pendingUser);
        doRegister(newUser, pendingUser.getReferralCode());

        pendingRepo.delete(pendingUser);
        log.info("Pending user deleted: {}", pendingUser.getUsername());

        AuthResponse response = authService.verifyOtpAndIssueToken(sessionId, otp);
        log.info("Registration complete for sessionId: {}", sessionId);

        return response;
    }

    @Override
    public User directRegister(User user, String referralCode) {
        log.info("Direct registration for username: {}", user.getUsername());

        userRepo.findByReferralCode(referralCode).ifPresent(user::setReferrer);

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            String generatedEmail = user.getUsername() + "@trustai.com";
            user.setEmail(generatedEmail);
            log.info("Email not provided. Generated default: {}", generatedEmail);
        }
//        Kyc kyc = new Kyc();
//        kyc.setEmail(user.getEmail());
//        kyc.setPhone(user.getMobile());
//        kyc.setFirstname(user.getUsername());
        //user.setKycInfo(kyc);

        User newUser = doRegister(user, referralCode);
        log.info("Direct registration completed for userId: {}", newUser.getId());

        return doRegister(user, referralCode);
    }

    private User doRegister(User user, String inviteCode) {
        log.info("Registering user: {}", user.getUsername());
        userRepo.findByReferralCode(inviteCode).ifPresent(user::setReferrer);

        User newUser = userRepo.save(user);
        log.info("User persisted with ID: {}", newUser.getId());

        // Generate a unique referral code
        log.info("Generating referralCode for userId: {}.....", newUser.getId());
        String referralCode = generateUniqueReferralCode();
        newUser.setReferralCode(referralCode);
        userRepo.save(newUser);
        log.info("Referral code generated and saved: {}", referralCode);

        if (newUser.getReferrer() != null) {
            log.info("User has referrer (ID: {}). Updating hierarchy...", newUser.getReferrer().getId());
            // TODO handle UserHierarchy Update
//            userHierarchyService.updateHierarchy(newUser.getReferrer().getId(), newUser.getId());
        }

        log.info("Publishing UserRegisteredEvent for userId: {}", newUser.getId());
        publisher.publishEvent(new UserRegisteredEvent(
                newUser.getId(),
                newUser.getReferrer() != null ? newUser.getReferrer().getId() : null
        ));

        return newUser;
    }

    private User mapFromPending(PendingUser pendingUser) {
        log.debug("Mapping PendingUser to User for: {}", pendingUser.getUsername());
        User newUser = new User();
        newUser.setUsername(pendingUser.getUsername());
        newUser.setPassword(pendingUser.getPasswordHash());
        newUser.setEmail(pendingUser.getEmail());
        newUser.setEmailVerified(true);
        newUser.setMobile(pendingUser.getMobile());
        return newUser;
    }

    private String generateUniqueReferralCode() {
        String code;
        int attempts = 0;
        do {
            code = ReferralCodeUtil.generate(MAX_REFERRAL_CODE_LENGTH);
            attempts++;
        } while (userRepo.existsByReferralCode(code));

        log.info("Generated unique referral code '{}' after {} attempt(s)", code, attempts);
        return code;
    }

    public HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }
}
