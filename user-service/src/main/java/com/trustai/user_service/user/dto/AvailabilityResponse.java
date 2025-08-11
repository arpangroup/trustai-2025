package com.trustai.user_service.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AvailabilityResponse {
    private boolean usernameAvailable;
    private boolean emailAvailable;
    private boolean mobileAvailable;
}
