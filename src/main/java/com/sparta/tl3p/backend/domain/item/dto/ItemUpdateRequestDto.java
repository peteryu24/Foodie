package com.sparta.tl3p.backend.domain.item.dto;

import com.sparta.tl3p.backend.domain.item.enums.ItemStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ItemUpdateRequestDto {

    private String     itemName;
    private BigDecimal price;
    private ItemStatus status;
    private String     description;
}
