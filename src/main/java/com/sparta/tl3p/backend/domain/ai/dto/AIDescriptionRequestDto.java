package com.sparta.tl3p.backend.domain.ai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AIDescriptionRequestDto {
    @NotNull
    private UUID itemId;
}