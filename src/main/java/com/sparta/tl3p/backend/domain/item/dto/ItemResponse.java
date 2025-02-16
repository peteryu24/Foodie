package com.sparta.tl3p.backend.domain.item.dto;

import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.entity.ItemStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ItemResponse {
    private final UUID          id;
    private final UUID          storeId;
    private final String        name;
    private final BigDecimal    price;
    private final String        description;
    private final ItemStatus    status;
    private final LocalDateTime createdAt;
    private final Long          createdBy;
    private final LocalDateTime updatedAt;
    private final Long          updatedBy;

    public static ItemResponse from(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .storeId(item.getStore().getId())
                .name(item.getName())
                .price(item.getPrice())
                .description(item.getDescription())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .createdBy(item.getCreatedBy())
                .updatedAt(item.getUpdatedAt())
                .updatedBy(item.getUpdatedBy())
                .build();
    }
}
