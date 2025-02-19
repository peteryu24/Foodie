package com.sparta.tl3p.backend.domain.payment.dto;

import com.sparta.tl3p.backend.domain.payment.entity.Payment;
import com.sparta.tl3p.backend.domain.payment.enums.PaymentMethod;
import com.sparta.tl3p.backend.domain.payment.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentResponseDto {
    private UUID paymentId;
    private PaymentStatus paymentStatus;
    private int amount;
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentDate;

    public PaymentResponseDto(Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.paymentStatus = payment.getPaymentStatus();
        this.amount = payment.getAmount();
        this.paymentMethod = payment.getPaymentMethod();
        this.paymentDate = payment.getPaymentDate();
    }

    // 결제 DTO를 Payment 엔티티로 복원 (모의 연계 시 사용)
    public Payment toPayment() {
        return Payment.builder()
                .paymentId(this.paymentId)
                .paymentStatus(this.paymentStatus)
                .amount(this.amount)
                .paymentMethod(this.paymentMethod)
                .paymentDate(this.paymentDate)
                .build();
    }
}
