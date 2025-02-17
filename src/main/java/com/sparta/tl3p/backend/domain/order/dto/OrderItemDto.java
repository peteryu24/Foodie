package com.sparta.tl3p.backend.domain.order.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.order.entity.OrderItem;
import com.sparta.tl3p.backend.domain.item.entity.Item;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private UUID itemId;
    private int quantity;
    private BigDecimal price;

    // 현재 DTO를 이용하여 OrderItem 엔티티로 변환 (Order, Item 필요)
    public OrderItem toEntity(Order order, Item item) {
        return OrderItem.builder()
                .orderItemId(UUID.randomUUID())
                .quantity(this.quantity)
                .price(this.price)
                .item(item)
                .order(order)
                .build();
    }
}
