package com.trustai.user_service.user.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustai.common_base.domain.user.User;
import com.trustai.common_base.dto.UserInfo;
import com.trustai.common_base.repository.user.UserRepository;
import com.trustai.user_service.user.entity.Kyc;
import com.trustai.user_service.user.exception.IdNotFoundException;
import com.trustai.user_service.user.mapper.UserMapper;
import com.trustai.user_service.user.service.UserProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
When a new user registers, bonuses can be propagated upwards through the referral hierarchy. Here's a simple approach:
    Direct Bonus: The referrer receives a direct bonus when a new user registers using their referral code
    Community Team Rebate: Calculate rebates based on the users level and propagate them upwards.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final int DEFAULT_PAGE_SIZE = 10;
//    private final TransactionService transactionService;
//    private final DepositService depositService;

    @Override
    public User createUser(User user, String referralCode) {
        log.info("creating user for username: {}", user.getUsername());
        Kyc kyc = new Kyc();
        user = userRepository.save(user);
        log.info("user created successfully with userId: {}", user.getId());

        // Update the Closure table for user relationship
        User referrer = userRepository.findByReferralCode(referralCode).orElse(null);
        if (referrer != null) {
            log.info("Referrer with ID: {} ====> updating the closure table....", referrer.getId());
            user.setReferrer(referrer);
        }
        return user;
    }

    @Override
    @CacheEvict(value = "users", key = "#user.id")
    public User updateUser(Long userId, Map<String, Object> fieldsToUpdate) {
        User user = getUserById(userId);

        // Only allow updates for firstname and lastname
        Set<String> allowedFields = Set.of("firstname", "lastname");

        fieldsToUpdate.forEach((key, value) -> {
            if (allowedFields.contains(key)) {
                Field field = ReflectionUtils.findField(User.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    Object convertedValue = new ObjectMapper().convertValue(value, field.getType());
                    ReflectionUtils.setField(field, user, convertedValue);
                }
            }
        });
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        userRepository.findById(user.getId()).ifPresent(u -> userRepository.save(user));
        return user;
    }

    @Override
    public User updateUserRank(Long userId, String newRankCode) {
        User user = userRepository.findById(userId).orElseThrow(()-> new IdNotFoundException("userId: " + userId + " not found"));
        user.setRankCode(newRankCode);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUserByIds(List<Long> userIds) {
        return userRepository.findByIdIn(userIds);
    }

    @Override
    public List<User> getUsers(User.AccountStatus status) {
        return userRepository.findByAccountStatus(status);
    }

    @Override
    public Page<UserInfo> getUsers(User.AccountStatus status, Integer page, Integer size) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<User> userPage;
        if (status != null) {
            userPage = userRepository.findByAccountStatus(status, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.map(mapper::mapTo);
    }


//    @Override
//    public List<User> getUserByIds(List<Long> userIds) {
//        return userRepository.findByIdIn(userIds);
//    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new IdNotFoundException("userId: " + userId + " not found"));
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        return false;
    }

//    @Override
//    public User getUserByReferralCode(String referralCode) {
//        return userRepository.findByReferralCode(referralCode).orElseThrow(() -> new IdNotFoundException("invalid referralCode"));
//    }

    @Override
    @Transactional
    public User deposit(long userId, BigDecimal amount, String remarks, String metaInfo) {
//        return depositService.deposit(userId, amount, remarks, metaInfo);
        return null;
    }

    @Override
    public boolean hasDeposit(Long userId) {
//        return depositService.hasDeposit(userId);
        return false;
    }

    @Override
    public BigDecimal getTotalDeposit(Long userId) {
        return null;
    }
}
