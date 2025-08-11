package com.trustai.common_base.exceptions;

import lombok.Getter;

@Getter
public class ValidationException  extends RuntimeException {
    private String errorCode;

    public ValidationException(String message) {
        super(message);
    }
    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
