package com.sparta.tl3p.backend.domain.order.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.order.dto.OrderCancelRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderDetailResponseDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderResponseDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderUpdateRequestDto;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OrderService {

    // in‑memory 주문 저장소 (실제 서비스에서는 Repository 사용)
    private final Map<UUID, Order> orderStore = new ConcurrentHashMap<>();

    // 모의 회원, 가게, 상품 데이터 저장소
    private final Map<Long, Member> memberMap = new HashMap<>();
    private final Map<UUID, Store> storeMap = new HashMap<>();

    // 모의 상품 데이터: 상품 ID → (상품명, 가격)
    private final Map<UUID, ItemInfo> productMap = new HashMap<>();

    // 내부 클래스: 상품 정보 (이름, 가격)
    private static class ItemInfo {
        String name;
        BigDecimal price;
        public ItemInfo(String name, BigDecimal price) {
            this.name = name;
            this.price = price;
        }
    }

//    // 기본 생성자에서 모의 데이터 초기화
//    public OrderService() {
//        // 회원 mock 데이터
//        Member member1 = new Member();
//        member1.setMemberId(1L);
//        member1.setUsername("hong");
//        memberMap.put(1L, member1);
//
//        Member member2 = new Member();
//        member2.setMemberId(2L);
//        member2.setUsername("kim");
//        memberMap.put(2L, member2);
//
//        // 가게 mock 데이터 (Store 엔티티의 Builder 사용)
//        Store store1 = Store.builder()
//                .name("Store1")
//                .content("Content1")
//                .member(member1)
//                .build();
//        Store store2 = Store.builder()
//                .name("Store2")
//                .content("Content2")
//                .member(member2)
//                .build();
//        storeMap.put(store1.getStoreId(), store1);
//        storeMap.put(store2.getStoreId(), store2);
//        System.out.println("Store1 UUID: " + store1.getStoreId());
//        System.out.println("Store2 UUID: " + store2.getStoreId());
//
//        // 상품 mock 데이터 (고정 UUID 사용)
//        UUID prod1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
//        UUID prod2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
//        UUID prod3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
//        productMap.put(prod1, new ItemInfo("상품명1", BigDecimal.valueOf(10000)));
//        productMap.put(prod2, new ItemInfo("상품명2", BigDecimal.valueOf(20000)));
//        productMap.put(prod3, new ItemInfo("상품명3", BigDecimal.valueOf(15000)));
//    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        Member member = memberMap.get(dto.getMemberId());
        if (member == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Store store = storeMap.get(dto.getStoreId());
        if (store == null) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
        // Order 엔티티 내부의 팩토리 메서드를 통해 주문 생성 (주문 아이템 처리 등은 추후 추가 가능)
        Order order = Order.createOrder(dto, member, store);
        order.setCreatedAt(LocalDateTime.now());
        orderStore.put(order.getOrderId(), order);
        return new OrderResponseDto(order);
    }

    @Transactional
    public OrderResponseDto updateOrder(UUID orderId, OrderUpdateRequestDto dto) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        order.updateOrder(dto);
        return new OrderResponseDto(order);
    }

    @Transactional
    public OrderResponseDto cancelOrder(UUID orderId, OrderCancelRequestDto dto) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        order.cancelOrder();
        return new OrderResponseDto(order);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(UUID orderId) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return new OrderDetailResponseDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(Long memberId) {
        return orderStore.values().stream()
                .filter(o -> o.getMember().getMemberId().equals(memberId))
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getStoreOrders(UUID storeId) {
        return orderStore.values().stream()
                .filter(o -> o.getStore().getStoreId().equals(storeId))
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    // 회원 주문 내에서 가게 이름 또는 주문한 상품 이름으로 검색
    @Transactional(readOnly = true)
    public List<OrderResponseDto> searchOrders(Long memberId, String storeName, String productName) {
        return orderStore.values().stream()
                .filter(order -> order.getMember().getMemberId().equals(memberId))
                .filter(order -> {
                    boolean matchesStore = true;
                    boolean matchesProduct = true;
                    if (StringUtils.hasText(storeName)) {
                        matchesStore = order.getStore().getName().toLowerCase().contains(storeName.toLowerCase());
                    }
                    if (StringUtils.hasText(productName)) {
                        matchesProduct = order.getOrderItems().stream()
                                .anyMatch(item -> item.getItem().getName().toLowerCase().contains(productName.toLowerCase()));
                    }
                    return matchesStore && matchesProduct;
                })
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }
}
