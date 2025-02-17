package com.sparta.tl3p.backend.domain.order.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelRequestDto {
    private String cancelReason;
}