package com.trustai.common_base.auth.repository;

import com.trustai.common_base.auth.entity.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, Long> {
}
