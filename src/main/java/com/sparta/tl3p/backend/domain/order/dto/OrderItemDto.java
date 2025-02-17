package com.sparta.tl3p.backend.domain.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private UUID itemId;
    private int quantity;
    private BigDecimal price;

}
