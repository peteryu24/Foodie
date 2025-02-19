package com.sparta.tl3p.backend.domain.payment.controller;

import com.sparta.tl3p.backend.common.dto.SuccessResponseDto;
import com.sparta.tl3p.backend.common.type.ResponseCode;
import com.sparta.tl3p.backend.domain.payment.dto.PaymentResponseDto;
import com.sparta.tl3p.backend.domain.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 결제 상세조회: GET /api/v1/payments/{paymentId}
    @GetMapping("/{paymentId}")
    public ResponseEntity<SuccessResponseDto> getPayment(@PathVariable UUID paymentId) {
        PaymentResponseDto response = new PaymentResponseDto(paymentService.getPaymentById(paymentId));
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.NS)
                        .message("Payment details")
                        .data(response)
                        .build()
        );
    }

    /**
     * 유저별 및 가게별 결제 내역 조회 API
     *
     * GET /api/v1/payments?userId={userId}  -> 유저별 결제 내역 조회
     * GET /api/v1/payments?storeId={storeId} -> 가게별 결제 내역 조회
     *
     * 파라미터가 둘 다 없는 경우에는 에러를 반환합니다.
     */
    @GetMapping
    public ResponseEntity<SuccessResponseDto> getPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) UUID storeId) {

        if (userId != null && storeId != null) {
            return ResponseEntity.badRequest().body(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.S)
                            .message("userId와 storeId 둘 다 동시에 제공할 수 없습니다.")
                            .data(new HashMap<>())
                            .build()
            );
        } else if (userId != null) {
            List<PaymentResponseDto> payments = paymentService.getPaymentsByUserId(userId);
            var data = new HashMap<String, Object>();
            data.put("userId", userId);
            data.put("payments", payments);
            return ResponseEntity.ok(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.NS)
                            .message("User payment details")
                            .data(data)
                            .build()
            );
        } else if (storeId != null) {
            List<PaymentResponseDto> payments = paymentService.getPaymentsByStoreId(storeId);
            var data = new HashMap<String, Object>();
            data.put("storeId", storeId);
            data.put("payments", payments);
            return ResponseEntity.ok(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.NS)
                            .message("Store payment details")
                            .data(data)
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.S)
                            .message("userId 또는 storeId 중 하나를 제공하세요")
                            .data(new HashMap<>())
                            .build()
            );
        }
    }
}
