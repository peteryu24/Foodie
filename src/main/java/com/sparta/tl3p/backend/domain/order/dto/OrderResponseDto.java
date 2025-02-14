package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.domain.order.enums.DataStatus;
import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {
    private UUID orderId;
    private OrderType orderType;
    private PaymentMethod paymentMethod;
    private String deliveryAddress;
    private String storeRequest;
    private DataStatus status;           // CREATED, UPDATED, DELETED
    private LocalDateTime createdAt;
    private UUID storeId;
    private Long userId;
}
