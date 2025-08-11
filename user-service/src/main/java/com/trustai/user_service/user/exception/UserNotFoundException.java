package com.trustai.user_service.user.exception;


import com.trustai.user_service.user.exception.base.UserValidationException;

public class UserNotFoundException extends UserValidationException {
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
