package com.trustai.common_base.auth.repository;

import com.trustai.common_base.auth.entity.VerificationToken;
import com.trustai.common_base.auth.entity.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByTypeAndTarget(VerificationType type, String target);
}
