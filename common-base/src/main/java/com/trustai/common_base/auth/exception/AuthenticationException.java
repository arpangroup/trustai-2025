package com.trustai.common_base.auth.exception;

public class AuthenticationException extends RuntimeException{
    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message) {
        super(message);
    }
}
