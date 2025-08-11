package com.trustai.user_service.user.exception;


import com.trustai.user_service.user.exception.base.UserValidationException;

public class InvalidRequestException extends UserValidationException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
