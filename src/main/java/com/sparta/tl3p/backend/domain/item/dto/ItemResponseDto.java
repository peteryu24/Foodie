package com.sparta.tl3p.backend.domain.item.dto;

import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.enums.ItemStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ItemResponseDto {
    private final UUID          id;
    private final UUID          storeId;
    private final String        storeName;
    private final String        itemName;
    private final String        description;
    private final BigDecimal    price;
    private final ItemStatus    status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ItemResponseDto of(Item item) {
        return ItemResponseDto.builder()
                .id(item.getItemId())
                .storeId(item.getStore().getStoreId())
                .storeName(item.getStore().getName())
                .itemName(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
