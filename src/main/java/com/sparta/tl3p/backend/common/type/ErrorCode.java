package com.sparta.tl3p.backend.common.type;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Member
    MEMBER_NOT_FOUND("M-001", "Member Not Found"),
    EMAIL_NOT_FOUND("M-002", "Member Email Not Found"),
    ROLE_NOT_FOUND("M-003", "Member Role Not Found"),
    USERNAME_DUPLICATE("M-004", "Username already exists"),

    // Store
    STORE_NOT_FOUND("S-001", "Store Not Found"),
    // Review
    REVIEW_NOT_FOUND("R-001", "Review Not Found"),
    // Item
    ITEM_NOT_FOUND("I-001", "Item Not Found"),
    // Order
    ORDER_NOT_FOUND("O-001", "Order Not Found"),
    // Payment
    PAYMENT_NOT_FOUND("P-001", "Payment Not Found"),
    PAYMENT_FAILED("P-002", "Payment Failed"),

    // Valid
    ARGUMENT_NOT_VALID("V-001", "Method Argument Not Valid"),

    // API
    API_UNEXPECTED_ERROR("A-001","API Error"),
    API_CALL_ERROR("A-002", "API Call Error"),
    REST_TEMPLATE_ERROR("A-003", "REST Template Error"),
    API_STATUS_ERROR("A-004", "API Status Error"),
    API_RESPONSE_PARSE_ERROR("A-005", "API Response Parse Error"),

    // Unexpected Exception
    UNEXPECTED_ERROR("D-001", "Unexpected error ");


    private final String code;
    private final String message;

    ErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
