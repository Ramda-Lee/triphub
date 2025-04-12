package com.triphub.auth.exception;

public class EmailAlreadyExistsException extends AuthenticationException {
    public EmailAlreadyExistsException(String email) {
        super("이미 존재하는 이메일입니다: " + email);
    }
} 