package com.trustai.common_base.auth.repository;

import com.trustai.common_base.auth.entity.RegistrationProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationProgressRepository extends JpaRepository<RegistrationProgress, Long> {
    Optional<RegistrationProgress> findByEmail(String email);
    Optional<RegistrationProgress> findByMobile(String mobile);
}
