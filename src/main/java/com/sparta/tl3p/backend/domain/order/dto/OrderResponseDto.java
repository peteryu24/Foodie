package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.common.type.Address;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderResponseDto {
    private UUID orderId;
    private String status;
    private Address deliveryAddress;

    public OrderResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.status = order.getStatus().name();
        this.deliveryAddress = order.getDeliveryAddress();
    }
}
