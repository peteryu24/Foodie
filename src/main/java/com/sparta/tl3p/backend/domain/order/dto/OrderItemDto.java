package com.sparta.tl3p.backend.domain.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private UUID id;          // PK
    private int quantity;
    private BigDecimal price;
    private UUID itemId;      // FK: 상품ID
    private UUID orderId;     // FK: 주문ID
    private Long userId;      // FK: 사용자ID
}
