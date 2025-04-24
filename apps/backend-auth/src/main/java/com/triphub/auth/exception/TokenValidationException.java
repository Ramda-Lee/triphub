package com.triphub.auth.exception;

public class TokenValidationException extends AuthenticationException {
    public TokenValidationException(String message) {
        super(message);
    }
} 