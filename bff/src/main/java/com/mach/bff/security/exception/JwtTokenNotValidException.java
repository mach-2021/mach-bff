package com.mach.bff.security.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenNotValidException extends AuthenticationException {

    private static final String MESSAGE = "The token is invalid or expired";

    public JwtTokenNotValidException() {
        super(MESSAGE);
    }

    public JwtTokenNotValidException(final String message) {
        super(message);
    }

    public JwtTokenNotValidException(final String message, Throwable cause) {
        super(message, cause);
    }

}
