package com.sparta.tl3p.backend.domain.order.dto;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateRequestDto {
    private List<OrderItemDto> items;
    private String store_request;
}
