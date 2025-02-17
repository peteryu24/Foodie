package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOrderListItemDto {
    private String orderId;
    private String memberId;  // 고객 ID (예:"CUST_1")
    private OrderType orderType;
    private PaymentMethod paymentMethod;
    private String deliveryAddress;
    private String store_request;
    private String createdAt;
}