package com.trustai.user_service.auth.request;

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
