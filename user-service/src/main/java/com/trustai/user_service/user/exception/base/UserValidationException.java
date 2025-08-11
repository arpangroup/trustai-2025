package com.trustai.user_service.user.exception.base;

public class UserValidationException extends RuntimeException{
    public UserValidationException() {
        super();
    }

    public UserValidationException(String message) {
        super(message);
    }
}
