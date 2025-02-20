package com.sparta.tl3p.backend.domain.order.dto;

import com.sparta.tl3p.backend.common.type.Address;
import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor // 단위 테스트 코드 작성 위해 열어두었습니다.
public class OrderRequestDto {
    private OrderType orderType;
    private PaymentMethod paymentMethod;
    private Address deliveryAddress;
    private String storeRequest;
    private Long memberId;
    private UUID storeId;
    private List<OrderItemRequestDto> items;
}
