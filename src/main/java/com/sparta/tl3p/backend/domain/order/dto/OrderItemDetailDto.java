package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.domain.order.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderItemDetailDto {
    private UUID orderItemId;
    private int quantity;
    private BigDecimal price;
    private UUID itemId;

    public OrderItemDetailDto(OrderItem orderItem) {
        this.orderItemId = orderItem.getOrderItemId();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
        // Item 엔티티의 식별자 타입이 UUID라고 가정
        this.itemId = orderItem.getItem().getItemId();
    }
}
