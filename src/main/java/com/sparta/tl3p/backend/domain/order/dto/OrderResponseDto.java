package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.common.type.Address;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.order.enums.DataStatus;
import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class OrderResponseDto {
    private UUID orderId;
    private OrderType orderType;
    private PaymentMethod paymentMethod;
    private Address deliveryAddress;
    private String storeRequest;
    private DataStatus status;
    private LocalDateTime createdAt;
    private UUID storeId;
    private Long userId;

    // Order 엔티티를 받아 필드를 초기화
    public OrderResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.orderType = order.getOrderType();
        this.paymentMethod = order.getPaymentMethod();
        this.deliveryAddress = order.getDeliveryAddress();
        this.storeRequest = order.getStoreRequest();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.storeId = order.getStore().getStoreId();
        this.userId = order.getMember().getMemberId();
    }
}
