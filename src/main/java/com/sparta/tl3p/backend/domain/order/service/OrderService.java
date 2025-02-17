package com.sparta.tl3p.backend.domain.order.service;

import com.sparta.tl3p.backend.domain.order.dto.*;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.order.entity.OrderItem;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OrderService {

    // in‑memory 주문 저장소 (실제 서비스에서는 Repository 사용)
    private final Map<UUID, Order> orderStore = new ConcurrentHashMap<>();

    // 모의 회원, 가게, 상품 데이터
    private final Map<Long, Member> memberMap = new HashMap<>();
    private final Map<UUID, Store> storeMap = new ConcurrentHashMap<>();

    // 모의 상품 데이터: 상품 ID → (상품명, 가격)
    private final Map<UUID, ItemInfo> productMap = new HashMap<>();

    private static class ItemInfo {
        String name;
        BigDecimal price;
        public ItemInfo(String name, BigDecimal price) {
            this.name = name;
            this.price = price;
        }
    }

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
//        // 가게 mock 데이터 (Store 엔티티의 builder 사용)
//        Store store1 = Store.builder()
//                .name("Store1")
//                .content("Content1")
//                .address("서울시 강남구")
//                .owner(member1)
//                .build();
//        Store store2 = Store.builder()
//                .name("Store2")
//                .content("Content2")
//                .address("서울시 종로구")
//                .owner(member2)
//                .build();
//        storeMap.put(store1.getId(), store1);
//        storeMap.put(store2.getId(), store2);
//        System.out.println("Store1 UUID: " + store1.getId());
//        System.out.println("Store2 UUID: " + store2.getId());
//
//        // 상품 mock 데이터 (고정 UUID 사용)
//        UUID prod1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
//        UUID prod2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
//        UUID prod3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
//        productMap.put(prod1, new ItemInfo("상품명1", BigDecimal.valueOf(10000)));
//        productMap.put(prod2, new ItemInfo("상품명2", BigDecimal.valueOf(20000)));
//        productMap.put(prod3, new ItemInfo("상품명3", BigDecimal.valueOf(15000)));
//    }
//

    // 주문 생성 – 엔티티의 factory 메소드 활용 후 OrderResponseDto로 반환
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        Member member = memberMap.get(dto.getMemberId());
        if (member == null) {
            throw new RuntimeException("Member not found");
        }
        Store store = storeMap.get(dto.getStoreId());
        if (store == null) {
            throw new RuntimeException("Store not found");
        }
        // 주문 아이템 생성 (dto의 items를 OrderItem 엔티티로 변환)
        List<OrderItem> orderItems = new ArrayList<>();
        if (dto.getItems() != null) {
            orderItems = dto.getItems().stream().map(itemDto -> {
                ItemInfo info = productMap.get(itemDto.getItemId());
                if (info == null) {
                    throw new RuntimeException("Product not found: " + itemDto.getItemId());
                }
                Item item = Item.builder()
                        .name(info.name)
                        .price(info.price)
                        .description("상품 설명")
                        .build();
                // 일단 order 참조 없이 생성 (후에 Order 생성 후 설정)
                return itemDto.toEntity(null, item);
            }).collect(Collectors.toList());
        }
        Order order = Order.createOrder(dto, member, store, orderItems);
        // 생성된 orderItems의 order 참조 설정
        order.getOrderItems().forEach(oi -> oi.setOrder(order));
        order.setCreatedAt(LocalDateTime.now());
        orderStore.put(order.getOrderId(), order);
        return new OrderResponseDto(order);
    }

    public OrderResponseDto updateOrder(UUID orderId, OrderUpdateRequestDto dto) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        List<OrderItem> orderItems = dto.getItems().stream().map(itemDto -> {
            ItemInfo info = productMap.get(itemDto.getItemId());
            if (info == null) {
                throw new RuntimeException("Product not found: " + itemDto.getItemId());
            }
            Item item = Item.builder()
                    .name(info.name)
                    .price(info.price)
                    .description("상품 설명")
                    .build();
            return itemDto.toEntity(order, item);
        }).collect(Collectors.toList());
        order.updateOrderItems(orderItems, dto.getStoreRequest());
        orderStore.put(orderId, order);
        return new OrderResponseDto(order);
    }

    public OrderResponseDto cancelOrder(UUID orderId, OrderCancelRequestDto dto) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        order.cancelOrder();
        orderStore.put(orderId, order);
        return new OrderResponseDto(order);
    }

    public OrderDetailResponseDto getOrderDetail(UUID orderId) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        return new OrderDetailResponseDto(order);
    }

    public List<UserOrderListItemDto> getUserOrders(Long memberId) {
        return orderStore.values().stream()
                .filter(order -> order.getMember().getMemberId().equals(memberId))
                .map(UserOrderListItemDto::new)
                .collect(Collectors.toList());
    }

    public List<StoreOrderListItemDto> getStoreOrders(UUID storeId) {
        return orderStore.values().stream()
                .filter(order -> order.getStore().getId().equals(storeId))
                .map(StoreOrderListItemDto::new)
                .collect(Collectors.toList());
    }
}
