package com.trustai.common_base.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String traceId = MDC.get("traceId");
        String userId = "1"; // Or extract from JWT
        String path = request.getRequestURI();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");


        String body = String.format("""
                {
                    "status": 403,
                    "error": "Access Denied",
                    "message": "You do not have permission to access this resource.",
                    "traceId": "%s",
                    "userId": "%s",
                    "path": "%s"
                }
                """, traceId, userId, path);

        response.getWriter().write(body);
    }
}
