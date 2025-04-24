package com.triphub.auth.exception;

public class TokenExpiredException extends TokenValidationException {
    public TokenExpiredException(String message) {
        super(message);
    }
} 