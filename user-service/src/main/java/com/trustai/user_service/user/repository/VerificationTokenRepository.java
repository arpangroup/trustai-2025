package com.trustai.user_service.user.repository;

import com.trustai.user_service.user.entity.VerificationToken;
import com.trustai.user_service.user.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByTypeAndTarget(VerificationType type, String target);
}
