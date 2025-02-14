package com.sparta.tl3p.backend.domain.payment.dto;

import com.sparta.tl3p.backend.domain.payment.enums.PaymentMethod;
import com.sparta.tl3p.backend.domain.payment.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
    private UUID paymentId;
    private UUID orderId;
    private PaymentStatus paymentStatus;
    private int amount;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
}
