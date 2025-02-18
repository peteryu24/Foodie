package com.sparta.tl3p.backend.domain.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class OrderItemRequestDto {
    private UUID itemId;
    private int quantity;
}
