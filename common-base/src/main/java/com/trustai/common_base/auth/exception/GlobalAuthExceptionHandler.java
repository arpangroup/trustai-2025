package com.trustai.common_base.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
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

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        String path = request.getRequestURI();

        Map<String, Object> body = Map.of(
                "status", 403,
                "error", "Access Denied",
                "message", "You do not have permission to access this resource.",
                "traceId", traceId,
                "path", path
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
}
