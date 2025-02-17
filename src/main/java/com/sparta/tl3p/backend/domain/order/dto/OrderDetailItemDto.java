package com.sparta.tl3p.backend.domain.order.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailItemDto {
    private UUID itemId;
    private int quantity;
    private String itemName;
    private BigDecimal price;
}