package com.triphub.auth.exception;

import com.triphub.auth.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException e,
            HttpServletRequest request) {
        return createErrorResponse(
            ErrorCode.EMAIL_ALREADY_EXISTS,
            e.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            InvalidCredentialsException e,
            HttpServletRequest request) {
        return createErrorResponse(
            ErrorCode.INVALID_CREDENTIALS,
            e.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ErrorResponse> handleTokenValidationException(
            TokenValidationException e,
            HttpServletRequest request) {
        return createErrorResponse(
            ErrorCode.TOKEN_VALIDATION_FAILED,
            e.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(
            TokenExpiredException e,
            HttpServletRequest request) {
        return createErrorResponse(
            ErrorCode.TOKEN_EXPIRED,
            e.getMessage(),
            request.getRequestURI()
        );
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(
            ErrorCode errorCode,
            String message,
            String path) {
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .error(errorCode.getCode())
            .message(message)
            .path(path)
            .build();

        return new ResponseEntity<>(response, errorCode.getStatus());
    }
} 