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
import org.springframework.security.core.Authentication;
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
     * - Authentication을 통해 로그인한 회원의 ID를 획득하여 주문 생성
     */
    @PostMapping
    public ResponseEntity<SuccessResponseDto> createOrder(@RequestBody OrderRequestDto request,
                                                          Authentication authentication) {
        Long memberId = Long.parseLong(authentication.getName());
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
     * - 주문 ID와 수정 요청 데이터를 받아 주문을 수정합니다.
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<SuccessResponseDto> updateOrder(@PathVariable String orderId,
                                                          @RequestBody OrderUpdateRequestDto request,
                                                          Authentication authentication) {
        Long memberId = Long.parseLong(authentication.getName());
        OrderResponseDto response = orderService.updateOrder(UUID.fromString(orderId), request, memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("주문 수정")
                        .data(response)
                        .build()
        );
    }

    /**
     * 주문 취소 API
     * - 주문 ID와 취소 요청 데이터를 받아 주문 취소를 진행합니다.
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<SuccessResponseDto> cancelOrder(@PathVariable String orderId,
                                                          @RequestBody OrderCancelRequestDto request,
                                                          Authentication authentication) {
        Long memberId = Long.parseLong(authentication.getName());
        OrderResponseDto response = orderService.cancelOrder(UUID.fromString(orderId), request, memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("주문 취소")
                        .data(response)
                        .build()
        );
    }

    /**
     * 주문 상세 조회 API
     * - 주문 ID를 통해 해당 주문의 상세 정보를 반환합니다.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<SuccessResponseDto> getOrderDetail(@PathVariable String orderId,
                                                             Authentication authentication) {
        Long memberId = Long.parseLong(authentication.getName());
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
     * - 추가로 storeName 또는 productName이 제공되면 QueryDSL 기반 검색을 수행합니다.
     */
    @GetMapping
    public ResponseEntity<SuccessResponseDto> getOrders(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String productName,
            Authentication authentication) {

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
            // 가게 주문 조회 시, 로그인한 회원이 가게의 소유자인지 검증
            Long authMemberId = Long.parseLong(authentication.getName());
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
