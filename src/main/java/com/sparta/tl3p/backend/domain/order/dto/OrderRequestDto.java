package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderRequestDto {
    private OrderType orderType;
    private PaymentMethod paymentMethod;
    private Address deliveryAddress;
    private String storeRequest;
    private Long memberId;
    private UUID storeId;
    private List<OrderItemRequestDto> items;
}
