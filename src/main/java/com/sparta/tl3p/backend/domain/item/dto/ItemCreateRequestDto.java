package com.sparta.tl3p.backend.domain.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ItemCreateRequestDto {
    private UUID       storeId;
    private String     itemName;
    private BigDecimal price;
    private String     description;
}
