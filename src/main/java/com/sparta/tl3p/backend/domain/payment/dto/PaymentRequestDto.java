package com.sparta.tl3p.backend.domain.payment.dto;

import com.sparta.tl3p.backend.domain.payment.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private int amount;
    private PaymentMethod paymentMethod;
}
