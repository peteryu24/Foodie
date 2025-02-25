package com.sparta.tl3p.backend.domain.payment.dto;

import com.sparta.tl3p.backend.domain.payment.enums.PaymentMethod;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
}
