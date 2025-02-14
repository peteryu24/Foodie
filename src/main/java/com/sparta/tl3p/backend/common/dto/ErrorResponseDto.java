package com.sparta.tl3p.backend.common.dto;

import com.sparta.tl3p.backend.common.type.ResponseCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseDto {
    private final ResponseCode code;
    private final String message;

    public ErrorResponseDto(ResponseCode code, String message) {
        this.code = code;
        this.message = message;
    }
}