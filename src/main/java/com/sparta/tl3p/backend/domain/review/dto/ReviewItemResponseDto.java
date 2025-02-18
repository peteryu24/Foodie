package com.sparta.tl3p.backend.domain.review.dto;

import com.sparta.tl3p.backend.domain.item.entity.Item;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ReviewItemResponseDto {

    private final UUID itemId;
    private final String itemName;
    private final String description;

    public static ReviewItemResponseDto of(Item item) {
        return ReviewItemResponseDto.builder()
                .itemId(item.getItemId())
                .itemName(item.getName())
                .description(item.getDescription())
                .build();
    }
}
