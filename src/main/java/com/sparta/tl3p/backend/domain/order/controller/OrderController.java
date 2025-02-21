package com.sparta.tl3p.backend.domain.order.controller;

import com.sparta.tl3p.backend.common.dto.SuccessResponseDto;
import com.sparta.tl3p.backend.common.type.ResponseCode;
import com.sparta.tl3p.backend.domain.order.dto.OrderCancelRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderDetailResponseDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderResponseDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderUpdateRequestDto;
import com.sparta.tl3p.backend.domain.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 주문 생성 API
     * - @AuthenticationPrincipal을 통해 로그인한 회원의 ID(memberId)를 주입받아 주문을 생성합니다.
     */
    @PostMapping
    public ResponseEntity<SuccessResponseDto> createOrder(@RequestBody OrderRequestDto request,
                                                          @AuthenticationPrincipal Long memberId) {
        OrderResponseDto response = orderService.createOrder(request, memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("주문 완료")
                        .data(response)
                        .build()
        );
    }

    /**
     * 주문 수정 API
     * - 고객은 주문 생성 후 5분 이내에 수정 가능하며, 주문 상태(status) 변경은 불가능합니다.
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<SuccessResponseDto> updateOrder(@PathVariable String orderId,
                                                          @RequestBody OrderUpdateRequestDto request,
                                                          @AuthenticationPrincipal Long memberId) {
        OrderResponseDto response = orderService.updateOrder(UUID.fromString(orderId), request, memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("주문 수정 완료")
                        .data(response)
                        .build()
        );
    }

    /**
     * 주문 취소 API
     * - 고객은 주문 생성 후 5분 이내에 취소가 가능합니다.
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<SuccessResponseDto> cancelOrder(@PathVariable String orderId,
                                                          @RequestBody OrderCancelRequestDto request,
                                                          @AuthenticationPrincipal Long memberId) {
        OrderResponseDto response = orderService.cancelOrder(UUID.fromString(orderId), request, memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("주문 취소 완료")
                        .data(response)
                        .build()
        );
    }

    /**
     * 주문 상세 조회 API
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<SuccessResponseDto> getOrderDetail(@PathVariable String orderId,
                                                             @AuthenticationPrincipal Long memberId) {
        OrderDetailResponseDto detail = orderService.getOrderDetail(UUID.fromString(orderId), memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.NS)
                        .message("주문 상세 조회")
                        .data(detail)
                        .build()
        );
    }

    /**
     * 주문 조회 및 검색 API
     * - memberId가 제공되면 회원별 주문 조회
     * - storeId가 제공되면 해당 가게의 주문 조회 (가게 소유자 검증 포함)
     * - storeName 또는 productName이 제공되면 검색 처리
     */
    @GetMapping
    public ResponseEntity<SuccessResponseDto> getOrders(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String productName,
            @AuthenticationPrincipal Long authMemberId) {

        if (memberId != null && (storeName != null || productName != null)) {
            List<OrderResponseDto> orders = orderService.searchOrders(memberId, storeName, productName);
            return ResponseEntity.ok(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.NS)
                            .message("검색 주문 조회")
                            .data(orders)
                            .build()
            );
        } else if (memberId != null) {
            List<OrderResponseDto> orders = orderService.getUserOrders(memberId);
            return ResponseEntity.ok(
                    SuccessResponseDto.builder()
                            .code(ResponseCode.NS)
                            .message("회원 주문 조회")
                            .data(orders)
                            .build()
            );
        } else if (storeId != null) {
            List<OrderResponseDto> orders = orderService.getStoreOrders(storeId, authMemberId);
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
