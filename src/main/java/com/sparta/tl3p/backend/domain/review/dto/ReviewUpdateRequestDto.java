package com.sparta.tl3p.backend.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewUpdateRequestDto {
    private final String content;
    private final double score;
}
