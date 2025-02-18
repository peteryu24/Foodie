package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.common.type.Address;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class OrderDetailResponseDto {
    private String orderId;
    private String memberId;
    private String storeId;
    private Object orderType;
    private Object paymentMethod;
    private Address deliveryAddress;
    private String storeRequest;
    private List<OrderDetailItemDto> items;
    private String createdAt;

    // Order 엔티티를 받아 DTO 필드를 초기화하는 생성자
    public OrderDetailResponseDto(Order order) {
        this.orderId = order.getOrderId().toString();
        this.memberId = order.getMember().getMemberId().toString();
        this.storeId = order.getStore().getStoreId().toString();
        this.orderType = order.getOrderType();
        this.paymentMethod = order.getPaymentMethod();
        this.deliveryAddress = order.getDeliveryAddress();
        this.storeRequest = order.getStoreRequest();
        this.items = order.getOrderItems().stream()
                .map(oi -> OrderDetailItemDto.builder()
                        .itemId(oi.getItem().getId())
                        .quantity(oi.getQuantity())
                        .itemName(oi.getItem().getName())
                        .price(oi.getPrice())
                        .build())
                .collect(Collectors.toList());
        this.createdAt = order.getCreatedAt().toString();
    }
}
