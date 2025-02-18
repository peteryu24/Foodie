package com.sparta.tl3p.backend.domain.order.controller;

import com.sparta.tl3p.backend.common.dto.SuccessResponseDto;
import com.sparta.tl3p.backend.common.type.ResponseCode;
import com.sparta.tl3p.backend.domain.order.dto.*;
import com.sparta.tl3p.backend.domain.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<SuccessResponseDto> createOrder(@RequestBody OrderRequestDto request) {
        OrderResponseDto response = orderService.createOrder(request);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("주문 완료")
                        .data(response)
                        .build()
        );
    }

    // 주문 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<SuccessResponseDto> updateOrder(@PathVariable String orderId,
                                                          @RequestBody OrderUpdateRequestDto request) {
        OrderResponseDto response = orderService.updateOrder(UUID.fromString(orderId), request);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("주문 수정")
                        .data(response)
                        .build()
        );
    }

    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<SuccessResponseDto> cancelOrder(@PathVariable String orderId,
                                                          @RequestBody OrderCancelRequestDto request) {
        OrderResponseDto response = orderService.cancelOrder(UUID.fromString(orderId), request);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("주문 취소")
                        .data(response)
                        .build()
        );
    }

    // 주문 상세조회
    @GetMapping("/{orderId}")
    public ResponseEntity<SuccessResponseDto> getOrderDetail(@PathVariable String orderId) {
        OrderDetailResponseDto detail = orderService.getOrderDetail(UUID.fromString(orderId));
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.NS)
                        .message("주문 상세 조회")
                        .data(detail)
                        .build()
        );
    }

    // 회원별, 가게별 및 검색 (가게 이름, 상품 이름) 주문조회
    @GetMapping
    public ResponseEntity<SuccessResponseDto> getOrders(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String productName) {

        if (memberId != null && (storeName != null || productName != null)) {
            var orders = orderService.searchOrders(memberId, storeName, productName);
            return ResponseEntity.ok(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.NS)
                            .message("검색 주문 조회")
                            .data(orders)
                            .build()
            );
        } else if (memberId != null) {
            var orders = orderService.getUserOrders(memberId);
            return ResponseEntity.ok(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.NS)
                            .message("회원 주문 조회")
                            .data(orders)
                            .build()
            );
        } else if (storeId != null) {
            var orders = orderService.getStoreOrders(storeId);
            return ResponseEntity.ok(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.NS)
                            .message("가게 주문 조회")
                            .data(orders)
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.S)
                            .message("memberId 또는 storeId 중 하나를 제공하세요")
                            .data(new HashMap<>())
                            .build()
            );
        }
    }
}
