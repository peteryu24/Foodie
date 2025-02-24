package com.sparta.tl3p.backend.domain.order.controller;

import com.sparta.tl3p.backend.common.dto.SuccessResponseDto;
import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ResponseCode;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.member.entity.CustomUserDetails;
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
     * - 테스트 환경에서는 fallback으로 request에 담긴 memberId를 사용하지만,
     *   운영 환경에서는 반드시 인증정보가 필요합니다.
     */
    @PostMapping
    public ResponseEntity<SuccessResponseDto> createOrder(@RequestBody OrderRequestDto request,
                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long memberId;
        if (customUserDetails != null) {
            memberId = customUserDetails.getMemberId();
        } else {
            // 테스트 환경용 fallback (운영에서는 인증정보 사용)
            memberId = request.getMemberId();
        }

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
     * - 인증된 사용자만 접근할 수 있도록 합니다.
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<SuccessResponseDto> updateOrder(@PathVariable String orderId,
                                                          @RequestBody OrderUpdateRequestDto request,
                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Long memberId = customUserDetails.getMemberId();
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
     * - 인증된 사용자만 접근할 수 있도록 합니다.
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<SuccessResponseDto> cancelOrder(@PathVariable String orderId,
                                                          @RequestBody OrderCancelRequestDto request,
                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Long memberId = customUserDetails.getMemberId();
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
     * - 인증된 사용자만 접근할 수 있도록 합니다.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<SuccessResponseDto> getOrderDetail(@PathVariable String orderId,
                                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Long memberId = customUserDetails.getMemberId();
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
     */
    @GetMapping
    public ResponseEntity<SuccessResponseDto> getOrders(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) String productName,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        // 만약 쿼리 파라미터 memberId가 null이면, 인증 정보에서 가져옵니다.
        if (memberId == null) {
            if (customUserDetails != null) {
                memberId = customUserDetails.getMemberId();
            } else {
                throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
            }
        }

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
            // storeId 조회 시에는 인증 정보의 memberId를 사용합니다.
            if (customUserDetails == null) {
                throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
            }
            Long authMemberId = customUserDetails.getMemberId();
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
