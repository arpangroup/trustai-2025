package com.trustai.user_service.controller;

import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.dto.UserDetailsInfo;
import com.trustai.common_base.dto.UserInfo;
import com.trustai.common_base.repository.user.UserRepository;
import com.trustai.user_service.auth.service.RegistrationService;
import com.trustai.user_service.user.dto.PasswordUpdateRequest;
import com.trustai.user_service.user.mapper.UserMapper;
import com.trustai.user_service.user.service.UserAccountService;
import com.trustai.user_service.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserProfileService userService;
    private final UserAccountService userAccountService;
    private final RegistrationService registrationService;
    private final UserMapper mapper;
    private final UserRepository userRepository;

    /*@GetMapping
    public ResponseEntity<List<UserInfo>> users(
            @RequestParam User.AccountStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<User> users = userService.getUsers();
        List<UserInfo> userInfoList = users.stream().map(mapper::mapTo).toList();
        return ResponseEntity.ok(userInfoList);
    }*/

    @GetMapping
    public ResponseEntity<Page<UserInfo>> users(
            @RequestParam(required = false) User.AccountStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        Page<UserInfo> paginatedUsers = userService.getUsers(status, page, size);
        return ResponseEntity.ok(paginatedUsers);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsInfo> getUserInfoDetails(@PathVariable Long userId) {
        log.info("getUserInfo for User ID: {}......", userId);
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(mapper.mapToDetails(user));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUserInfo(@PathVariable Long userId, @RequestBody Map<String, Object> fieldsToUpdate) {
        log.info("updateUserInfo for User ID: {}, fieldsToUpdate: {}......", userId, fieldsToUpdate);
        return ResponseEntity.ok(userService.updateUser(userId, fieldsToUpdate));
    }

    @PutMapping("/{userId}/account-status")
    public ResponseEntity<User> updateAccountStatus(@PathVariable Long userId, @RequestParam User.AccountStatus status) {
        User user = userAccountService.updateAccountStatus(userId, status);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/transaction-status")
    public ResponseEntity<User> updateTransactionStatus(@PathVariable Long userId,
                                        @RequestParam(required = false) User.TransactionStatus depositStatus,
                                        @RequestParam(required = false) User.TransactionStatus withdrawStatus,
                                        @RequestParam(required = false) User.TransactionStatus sendMoneyStatus
    ) {
        User user =  userAccountService.updateTransactionStatus(userId, depositStatus, withdrawStatus, sendMoneyStatus);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/update-password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId, @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        log.info("Updating password for user ID: {}", userId);
        boolean success = userService.updatePassword(
                userId,
                passwordUpdateRequest.getOldPassword(),
                passwordUpdateRequest.getNewPassword()
        );

        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // or UNAUTHORIZED if applicable
        }
        return ResponseEntity.ok().build(); // 200 OK if successful
    }

}
