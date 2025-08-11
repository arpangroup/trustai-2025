package com.trustai.common_base.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.trustai.common_base")
public class SecurityExceptionHandler {

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
}
