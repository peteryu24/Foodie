package com.sparta.tl3p.backend.domain.item.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ItemCreateRequest {

    private UUID storeId;

    private String name;

    private BigDecimal price;

    private String description;
}
