package com.trustai.common_base.auth.exception;

import com.trustai.common_base.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@Order(1)
//@RestControllerAdvice(basePackages = "com.trustai.common_base")
public class GlobalAuthExceptionHandler {

    @ExceptionHandler({BadCredentialsException.class, UnsupportedAuthFlowException.class})
    public ResponseEntity<Map<String, Object>> handleSecurityAuthExceptions(RuntimeException ex) {
        String errorType = (ex instanceof BadCredentialsException)
                ? "Bad credential"
                : "Unsupported authentication flow";

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", errorType);
        body.put("message", ex.getMessage());

        //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, Object> handleAccessDenied(HttpServletRequest request, AccessDeniedException ex) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = "N/A";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "anonymous";
        List<String> roles = Collections.emptyList();

        if (authentication != null && authentication.isAuthenticated()) {
            userId = authentication.getName();
            if (authentication.getAuthorities() != null) {
                roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
            }
        }

        String path = request.getRequestURI();

        // Build map manually, null safe
        Map<String, Object> body = new HashMap<>();
        body.put("status", 403);
        body.put("error", "Access Denied");
        body.put("message", "You do not have permission to access this resource.");
        body.put("traceId", traceId);
        body.put("userId", userId);
        body.put("roles", roles);
        body.put("path", path);

        return body;
    }


    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
            org.springframework.security.authorization.AuthorizationDeniedException ex, WebRequest request) {

        String traceId = MDC.get("traceId");
        String userId = "1"; // TODO: get actual user from SecurityContext if needed
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

//        log.warn("AuthorizationDeniedException: traceId={}, userId={}, path={}, message={}",
//                traceId, userId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You do not have permission to access this resource.",
                path
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
