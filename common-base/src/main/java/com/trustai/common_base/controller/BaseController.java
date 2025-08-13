package com.trustai.common_base.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.trustai.trustai_common.security.service.CustomUserDetails;

public abstract class BaseController {
    @Autowired
    private HttpServletRequest request;

    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authenticated user found");
        }

        if (isInternalRequest(authentication)) {
            return getActingUserIdFromHeader();
        }

        return getExternalUserId(authentication);
    }

    protected boolean isAdmin() {
        return hasRole("ADMIN"); // Check role from security context
    }

    protected boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_" + role));
    }


    private boolean isInternalRequest(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_INTERNAL".equals(a.getAuthority()));
    }

    private Long getActingUserIdFromHeader() {
        String actingUserId = request.getHeader("X-Acting-User-Id");
        if (actingUserId == null || actingUserId.isBlank()) {
            throw new IllegalArgumentException("Missing acting user ID for internal call");
        }
        try {
            return Long.parseLong(actingUserId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid acting user ID format", ex);
        }
    }

    private Long getExternalUserId(Authentication authentication) {
        Object details = authentication.getDetails();
        if (!(details instanceof CustomUserDetails)) {
            throw new IllegalStateException("Authentication details are not CustomUserDetails");
        }
        return ((CustomUserDetails) details).getId();
    }
}
