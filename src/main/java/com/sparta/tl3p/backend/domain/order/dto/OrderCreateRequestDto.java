package com.sparta.tl3p.backend.domain.order.dto;


import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequestDto {
    private OrderType orderType;           // ONLINE, IN_STORE
    private PaymentMethod paymentMethod;   // CARD
    private String deliveryAddress;
    private String storeRequest;
    private UUID storeId;                  // FK: p_order.store_id
    private Long userId;                   // FK: p_order.user_id
}
