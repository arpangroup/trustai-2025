package com.trustai.common_base.auth.exception;

public class UnsupportedAuthFlowException extends AuthException {
    public UnsupportedAuthFlowException(String flow) {
        super("Unknown auth flow: " + flow);
    }
}
