package com.trustai.common_base.auth.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;

    /**
     * Optional. If "2step" client wants two-step flow; otherwise "password" flow.
     * Could be an enum in real system.
     */
    private String flow; // "password" or "2step"
}
