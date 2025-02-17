package com.sparta.tl3p.backend.common.type;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Member
    MEMBER_NOT_FOUND("M-001", "Member Not Found"),
    EMAIL_NOT_FOUND("M-002", "Member Email Not Found"),
    ROLE_NOT_FOUND("M-003", "Member Role Not Found"),

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
