package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.domain.order.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrderDetailResponseDto {
    private UUID orderId;
    private String status;
    private String deliveryAddress;
    private String storeRequest;
    private List<OrderItemDetailDto> items;

    public OrderDetailResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.status = order.getStatus().name();
        this.deliveryAddress = order.getDeliveryAddress();
        this.storeRequest = order.getStoreRequest();
        this.items = order.getOrderItems().stream()
                .map(OrderItemDetailDto::new)
                .collect(Collectors.toList());
    }
}
