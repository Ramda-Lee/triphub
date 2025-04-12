package com.triphub.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH-001", "이미 존재하는 이메일입니다"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-002", "잘못된 인증 정보입니다"),
    TOKEN_VALIDATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-003", "토큰 검증에 실패했습니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-004", "만료된 토큰입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-005", "사용자를 찾을 수 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
} 