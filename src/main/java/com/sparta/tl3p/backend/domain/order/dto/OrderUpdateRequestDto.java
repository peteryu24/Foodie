package com.sparta.tl3p.backend.domain.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderUpdateRequestDto {
    private String storeRequest;
    private List<OrderItemRequestDto> items;
}
