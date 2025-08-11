package com.trustai.user_service.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleKycInfo {
    private Long kycId;
    private String fullname;
    private String documentType;
    private String createdAt;
    private String status;
}
