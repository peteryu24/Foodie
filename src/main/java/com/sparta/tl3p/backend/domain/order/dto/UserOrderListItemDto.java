package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrderListItemDto {
    private String orderId;
    private String storeId;
    private OrderType orderType;
    private PaymentMethod paymentMethod;
    private String deliveryAddress;
    private String store_request;
    private String createdAt;
}
