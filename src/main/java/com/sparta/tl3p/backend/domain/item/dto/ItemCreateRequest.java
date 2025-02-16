package com.sparta.tl3p.backend.domain.item.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemCreateRequest {

    private String name;

    private BigDecimal price;

    private String description;
}
