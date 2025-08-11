package com.trustai.common_base.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class InitiateRegistrationRequest {
    public String email;
    public String username;
    public String mobile;
}
