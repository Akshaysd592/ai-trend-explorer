package com.aitrend.auth.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Account already exists for email: " + email);
    }
}
