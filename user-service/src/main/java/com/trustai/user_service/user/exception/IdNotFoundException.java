package com.trustai.user_service.user.exception;


import com.trustai.user_service.user.exception.base.UserValidationException;

public class IdNotFoundException extends UserValidationException {
    public IdNotFoundException() {
        super("userId not found");
    }

    public IdNotFoundException(String message) {
        super(message);
    }
}
