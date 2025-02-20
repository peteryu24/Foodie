package com.sparta.tl3p.backend.domain.payment.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.payment.dto.PaymentRequestDto;
import com.sparta.tl3p.backend.domain.payment.dto.PaymentResponseDto;
import com.sparta.tl3p.backend.domain.payment.entity.Payment;
import com.sparta.tl3p.backend.domain.payment.enums.PaymentStatus;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    // In‑memory 결제 저장소 (실제 서비스에서는 Repository 사용)
    private final Map<UUID, Payment> paymentStore = new ConcurrentHashMap<>();

    /**
     * 외부 PG사 API와 연동하는 결제 요청을 모의로 처리합니다.
     * PaymentRequestDto를 받아 결제 승인(모의) 후 PaymentResponseDto를 반환합니다.
     */
    public PaymentResponseDto requestPayment(Order order, PaymentRequestDto requestDto) {
        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID()) // 결제 ID 생성
                .amount(requestDto.getAmount())
                .paymentMethod(requestDto.getPaymentMethod())
                .paymentStatus(PaymentStatus.SUCCESS) // 모의 승인 처리 (카드 결제만 지원)
                .paymentDate(LocalDateTime.now())
                .order(order)
                .build();
        paymentStore.put(payment.getPaymentId(), payment);
        return new PaymentResponseDto(payment);
    }

    public Payment getPaymentById(UUID paymentId) {
        Payment payment = paymentStore.get(paymentId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }

    // 가게별 결제 내역 조회 (Order의 Store의 storeId 기준)
    public List<PaymentResponseDto> getPaymentsByStoreId(UUID storeId) {
        return paymentStore.values().stream()
                .filter(payment -> payment.getOrder() != null &&
                        payment.getOrder().getStore() != null &&
                        payment.getOrder().getStore().getStoreId().equals(storeId))
                .map(PaymentResponseDto::new)
                .collect(Collectors.toList());
    }

    // 유저(멤버)별 결제 내역 조회 (Order의 Member의 memberId 기준)
    public List<PaymentResponseDto> getPaymentsByMemberId(Long memberId) {
        return paymentStore.values().stream()
                .filter(payment -> payment.getOrder() != null &&
                        payment.getOrder().getMember() != null &&
                        payment.getOrder().getMember().getMemberId().equals(memberId))
                .map(PaymentResponseDto::new)
                .collect(Collectors.toList());
    }
}
