package com.trustai.user_service.user.exception;


import com.trustai.user_service.user.exception.base.UserValidationException;

public class UserCreateException extends UserValidationException {
    public UserCreateException() {
        super();
    }

    public UserCreateException(String message) {
        super(message);
    }
}
