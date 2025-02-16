package com.sparta.tl3p.backend.domain.item.dto;

import com.sparta.tl3p.backend.domain.item.entity.ItemStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemUpdateRequest {

    private String name;

    private BigDecimal price;

    private String description;

    private ItemStatus itemStatus;
}
