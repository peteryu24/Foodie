package com.sparta.tl3p.backend.common.dto;

import com.sparta.tl3p.backend.common.type.ResponseCode;
import lombok.Getter;

import lombok.Builder;

@Getter
@Builder
public class SuccessResponseDto {
    private final ResponseCode code;
    private final String message;
    private final Object data;

    public SuccessResponseDto(ResponseCode code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
