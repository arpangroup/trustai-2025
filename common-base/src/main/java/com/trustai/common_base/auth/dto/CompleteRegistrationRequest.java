package com.trustai.common_base.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class CompleteRegistrationRequest {
    public String email;
    public String password;
    public String confirmPassword;
    public String referralCode;
}
