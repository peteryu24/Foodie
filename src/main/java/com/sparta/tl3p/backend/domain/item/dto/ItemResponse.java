package com.sparta.tl3p.backend.domain.item.dto;

import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.entity.ItemStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ItemResponse {
    private final UUID          id;
    private final String        name;
    private final BigDecimal    price;
    private final String        description;
    private final ItemStatus    status;
    private final LocalDateTime createdAt;
    private final Long          createdBy;
    private final LocalDateTime updatedAt;
    private final Long          updatedBy;

    @Builder
    private ItemResponse(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
        this.description = item.getDescription();
        this.status = item.getStatus();
        this.createdAt = item.getCreatedAt();
        this.createdBy = item.getCreatedBy();
        this.updatedAt = item.getUpdatedAt();
        this.updatedBy = item.getUpdatedBy();
    }

    public static ItemResponse of(Item item) {
        return ItemResponse.builder()
                .item(item)
                .build();
    }
}
