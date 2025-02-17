package com.sparta.tl3p.backend.domain.order.dto;


import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponseDto {
    private String orderId;
    private String memberId;
    private String storeId;
    private OrderType orderType;
    private PaymentMethod paymentMethod;
    private String deliveryAddress;
    private String store_request;
    private List<OrderDetailItemDto> items;
    private String createdAt;
}
