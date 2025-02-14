package com.sparta.tl3p.backend.domain.payment.dto;

import com.sparta.tl3p.backend.domain.payment.enums.PaymentMethod;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateRequestDto {
    private UUID orderId;            // FK: p_payment.order_id
    private int amount;
    private PaymentMethod paymentMethod; // CARD
}