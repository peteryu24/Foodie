package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import java.util.List;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {
    private Long memberId;       // 주문한 회원 ID
    private UUID storeId;        // 주문 대상 가게 ID
    private OrderType orderType; // ONLINE, IN_STORE 등
    private PaymentMethod paymentMethod; // 결제 수단 (예: CARD)
    private String deliveryAddress;
    private String storeRequest;
    private List<OrderItemDto> items;  // 주문 상품 리스트 추가
}
