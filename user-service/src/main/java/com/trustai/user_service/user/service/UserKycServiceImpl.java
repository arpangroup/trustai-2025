package com.trustai.user_service.user.service;

import com.trustai.user_service.user.dto.KycApproveRequest;
import com.trustai.user_service.user.dto.KycUpdateRequest;
import com.trustai.user_service.user.dto.SimpleKycInfo;
import com.trustai.user_service.user.entity.Kyc;
import com.trustai.user_service.user.mapper.KycMapper;
import com.trustai.user_service.user.repository.KycRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserKycServiceImpl implements UserKycService{
    private final KycRepository kycRepository;
    private final KycMapper mapper;

    @Override
    public Page<SimpleKycInfo> getAllKyc(Kyc.KycStatus status, Integer page, Integer size) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Kyc> kycPage;
        if (status != null) {
            kycPage = kycRepository.findByStatus(status, pageable);
        } else {
            kycPage = kycRepository.findAll(pageable);
        }
        return kycPage.map(mapper::mapTo);
    }

    @Override
    public Kyc getKycById(Long kycId) {
        return kycRepository.findById(kycId).orElseThrow(() -> new EntityNotFoundException("Kyc ID: " + kycId + " not found"));
    }


    @Override
    public Kyc updateKyc(Long userId, KycUpdateRequest request) {
        return null;
    }

    @Override
    public Kyc verifyKyc(Long userId, KycApproveRequest request) {
        return null;
    }
}
