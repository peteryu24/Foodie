package com.sparta.tl3p.backend.common.type;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Member
    MEMBER_NOT_FOUND("M-001", "Member Not Found"),
    EMAIL_NOT_FOUND("M-002", "Member Email Not Found"),
    ROLE_NOT_FOUND("M-003", "Member Role Not Found"),
    USERNAME_DUPLICATE("M-004", "Username already exists"),
    PASSWORD_MISMATCH("M-005", "비밀번호가 일치하지 않습니다"),
    INVALID_JWT_TOKEN("M-006", "유효하지 않은 JWT 토큰입니다."),

    // Store

    // Review
    REVIEW_NOT_FOUND("R-001", "Review Not Found"),
    // Item

    // Order

    // Payment

    // Valid
    ARGUMENT_NOT_VALID("V-001", "Method Argument Not Valid"),

    // Unexpected Exception
    UNEXPECTED_ERROR("D-001", "Unexpected error ");


    private final String code;
    private final String message;

    ErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
