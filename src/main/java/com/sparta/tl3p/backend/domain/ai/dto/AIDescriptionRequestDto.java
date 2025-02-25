package com.sparta.tl3p.backend.domain.ai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AIDescriptionRequestDto {
    @NotNull
    private UUID itemId;
}