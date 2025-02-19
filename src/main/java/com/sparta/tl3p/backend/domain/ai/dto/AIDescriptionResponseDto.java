package com.sparta.tl3p.backend.domain.ai.dto;

import com.sparta.tl3p.backend.domain.ai.entity.AIDescription;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AIDescriptionResponseDto {
    private String response;
    
    public static AIDescriptionResponseDto from(AIDescription aiDescription) {
        return AIDescriptionResponseDto.builder()
                .response(aiDescription.getResponse())
                .build();
    }
}
