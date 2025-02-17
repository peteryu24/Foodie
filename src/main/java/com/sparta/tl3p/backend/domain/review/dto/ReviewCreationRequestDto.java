package com.sparta.tl3p.backend.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReviewCreationRequestDto {
    private final UUID orderId;
    private final String content;
    private final double score;
}
