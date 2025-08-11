package com.trustai.user_service.user.repository;

import com.trustai.user_service.user.entity.RegistrationProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationProgressRepository extends JpaRepository<RegistrationProgress, Long> {
    Optional<RegistrationProgress> findByEmail(String email);
    Optional<RegistrationProgress> findByMobile(String mobile);
}
