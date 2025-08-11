package com.trustai.user_service.user.exception;


import com.trustai.user_service.user.exception.base.UserValidationException;

public class DuplicateRecordException extends UserValidationException {
    public DuplicateRecordException() {
        super();
    }

    public DuplicateRecordException(String message) {
        super(message);
    }
}
