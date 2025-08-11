package com.trustai.user_service.controller;

import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.repository.user.UserRepository;
import com.trustai.user_service.auth.request.CompleteRegistrationRequest;
import com.trustai.user_service.auth.request.InitiateRegistrationRequest;
import com.trustai.user_service.auth.request.RegistrationRequest;
import com.trustai.user_service.auth.request.VerifyEmailRequest;
import com.trustai.user_service.auth.service.RegistrationService;
import com.trustai.user_service.user.exception.IdNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {
    private final RegistrationService registrationService;
    private final HttpServletRequest servletRequest;
    private final UserRepository userRepository;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiate(@RequestBody InitiateRegistrationRequest request) {
        registrationService.initiateRegistration(request, servletRequest);
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest request) {
        registrationService.verifyEmail(request);
        return ResponseEntity.ok("Email verified");
    }

    @PostMapping("/complete")
    public ResponseEntity<?> complete(@RequestBody CompleteRegistrationRequest request) {
        registrationService.completeRegistration(request);
        return ResponseEntity.ok("Registration complete");
    }

    @PostMapping("/direct")
    public ResponseEntity<?> directRegister(@Valid @RequestBody RegistrationRequest request) {
        User user = new User(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setMobile(request.getEmail());

        registrationService.directRegister(user, request.getReferralCode());
        return ResponseEntity.ok("Registration complete");
    }

    @PostMapping("/add-users")
    public ResponseEntity<?> registerDummyUser() {
        addDummyUsers();
        return ResponseEntity.ok(Map.of("result", "success"));
    }

    private void addDummyUsers() {
        User root = userRepository.findById(1L).orElseThrow(()-> new IdNotFoundException("root userId not found"));

        User user1 = registerUser(new User("user1"), root.getReferralCode());
        User user2 = registerUser(new User("user2"), root.getReferralCode());
        User user3 = registerUser(new User("user3"), root.getReferralCode());

        User user1_1 = registerUser(new User("user1_1"), user1.getReferralCode());
        User user1_2 = registerUser(new User("user1_2"), user1.getReferralCode());
        User user2_1 = registerUser(new User("user2_1"), user2.getReferralCode());
        User user2_2 = registerUser(new User("user2_2"), user2.getReferralCode());
        User user3_1 = registerUser(new User("user3_1"), user3.getReferralCode());

        User user1_1_1 = registerUser(new User("user1_1_1"), user1_1.getReferralCode());
        User user1_1_2 = registerUser(new User("user1_1_2"), user1_1.getReferralCode());
        User user2_1_1 = registerUser(new User("user2_1_1"), user2_1.getReferralCode());
        User user2_1_2 = registerUser(new User("user2_1_2"), user2_1.getReferralCode());
        User user3_1_1 = registerUser(new User("user3_1_1"), user3_1.getReferralCode());

        User user1_1_1_1 = registerUser(new User("user1_1_1_1"), user1_1_1.getReferralCode());
        User user1_1_2_1 = registerUser(new User("user1_1_2_1"), user1_1_2.getReferralCode());
        User user1_1_2_2 = registerUser(new User("user1_1_2_2"), user1_1_2.getReferralCode());
        User user3_1_1_1 = registerUser(new User("user3_1_1_1"), user3_1_1.getReferralCode());


        User user1_1_2_2_1 = registerUser(new User("user1_1_2_2_1"), user1_1_2_2.getReferralCode());
        User user1_1_2_2_2 = registerUser(new User("user1_1_2_2_2"), user1_1_2_2.getReferralCode());
        User user3_1_1_1_1 = registerUser(new User("user3_1_1_1_1"), user3_1_1_1.getReferralCode());
        User user3_1_1_1_2 = registerUser(new User("user3_1_1_1_2"), user3_1_1_1.getReferralCode());


        User x = registerUser(new User("x"), user2_1_1.getReferralCode());
        User y = registerUser(new User("Y"), user2_1_1.getReferralCode());

        User x1 = registerUser(new User("x1"), x.getReferralCode());
        User x2 = registerUser(new User("x2"), x.getReferralCode());

        User y1 = registerUser(new User("y1"), y.getReferralCode());
        User y2 = registerUser(new User("y2"), y.getReferralCode());


        User x2_1 = registerUser(new User("x2_1"), x2.getReferralCode());
        User x2_2 = registerUser(new User("x2_2"), x2.getReferralCode());
    }

    private User registerUser(User user, final String referralCode){
        user.setRankCode("RANK_1");
        return registrationService.directRegister(user, referralCode);
    }
}
