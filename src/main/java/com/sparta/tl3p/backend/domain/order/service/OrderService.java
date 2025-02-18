package com.sparta.tl3p.backend.domain.order.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.order.dto.*;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.order.repository.OrderRepository;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    // 실제 서비스에서는 in‑memory 대신 OrderRepository를 사용합니다.
    private final OrderRepository orderRepository;

    // 모의 회원, 가게 데이터 (실제 서비스에서는 별도 Repository 주입)
    private final Member dummyMember = new Member();  // 예시용, 실제 값 주입 필요
    private final Store dummyStore = new Store();       // 예시용, 실제 값 주입 필요

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        // 실제 서비스라면 Member, Store 조회 Repository를 사용합니다.
        // 여기서는 간단하게 dummyMember, dummyStore 사용
        if(dto.getMemberId() == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if(dto.getStoreId() == null) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
        // Order 엔티티 생성
        Order order = Order.createOrder(dto, dummyMember, dummyStore);
        orderRepository.save(order);
        return new OrderResponseDto(order);
    }

    @Transactional
    public OrderResponseDto updateOrder(UUID orderId, OrderUpdateRequestDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.updateOrder(dto);
        return new OrderResponseDto(order);
    }

    @Transactional
    public OrderResponseDto cancelOrder(UUID orderId, OrderCancelRequestDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.cancelOrder();
        return new OrderResponseDto(order);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        return new OrderDetailResponseDto(order);
    }

    // 기존의 회원별/가게별 조회도 예시로 유지 (필요시 Querydsl 적용 가능)
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(Long memberId) {
        // 예시: memberId로 필터링한 후 DTO 변환 (실제 Querydsl이나 Repository 메서드를 사용 가능)
        List<Order> orders = orderRepository.findAll()
                .stream()
                .filter(o -> o.getMember().getMemberId().equals(memberId))
                .collect(Collectors.toList());
        return orders.stream().map(OrderResponseDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getStoreOrders(UUID storeId) {
        List<Order> orders = orderRepository.findAll()
                .stream()
                .filter(o -> o.getStore().getId().equals(storeId))
                .collect(Collectors.toList());
        return orders.stream().map(OrderResponseDto::new).collect(Collectors.toList());
    }

    // Querydsl을 활용한 검색: 회원 주문 내에서 가게 이름, 상품 이름으로 검색
    @Transactional(readOnly = true)
    public List<OrderResponseDto> searchOrders(Long memberId, String storeName, String productName) {
        List<Order> orders = orderRepository.searchOrders(memberId, storeName, productName);
        return orders.stream().map(OrderResponseDto::new).collect(Collectors.toList());
    }
}
