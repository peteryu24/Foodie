package com.sparta.tl3p.backend.domain.order.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.item.repository.ItemRepository;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.member.enums.Role;
import com.sparta.tl3p.backend.domain.member.repository.MemberRepository;
import com.sparta.tl3p.backend.domain.order.dto.OrderCancelRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderDetailResponseDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderResponseDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderUpdateRequestDto;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.order.entity.OrderItem;
import com.sparta.tl3p.backend.domain.order.repository.OrderRepository;
import com.sparta.tl3p.backend.domain.payment.dto.PaymentRequestDto;
import com.sparta.tl3p.backend.domain.payment.dto.PaymentResponseDto;
import com.sparta.tl3p.backend.domain.payment.entity.Payment;
import com.sparta.tl3p.backend.domain.payment.enums.PaymentMethod;
import com.sparta.tl3p.backend.domain.payment.enums.PaymentStatus;
import com.sparta.tl3p.backend.domain.payment.service.PaymentService;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (var itemDto : dto.getItems()) {
                var item = itemRepository.findById(itemDto.getItemId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
                totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            }
        }

        Order order = new Order(dto, member, store);
        order.setCreatedAt(LocalDateTime.now());

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<OrderItem> orderItems = dto.getItems().stream()
                    .map(itemDto -> {
                        var item = itemRepository.findById(itemDto.getItemId())
                                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
                        OrderItem orderItem = new OrderItem(itemDto, item);
                        // 1번 방법: OrderItem 생성 후 현재 주문(Order)을 할당
                        orderItem.setOrder(order);
                        return orderItem;
                    })
                    .collect(Collectors.toList());
            order.setOrderItems(orderItems);
        }

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setAmount(totalAmount);
        paymentRequestDto.setPaymentMethod(PaymentMethod.CARD);

        PaymentResponseDto paymentResponse = paymentService.requestPayment(order, paymentRequestDto);
        if (paymentResponse.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }
        Payment payment = paymentResponse.toPayment();
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder);
    }

    @Transactional
    public OrderResponseDto updateOrder(UUID orderId, OrderUpdateRequestDto dto, Long memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getRole() == Role.CUSTOMER) {
            if (!order.getMember().getMemberId().equals(memberId)) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED);
            }
            if (order.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.ORDER_TIME_OUT);
            }
        } else if (member.getRole() == Role.OWNER) {
            Store store = order.getStore();
            if (!store.getMember().getMemberId().equals(memberId)) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED);
            }
        }
        // 관리자나 최고관리자는 전체 수정 허용

        // 주문의 storeRequest 업데이트
        order.setStoreRequest(dto.getStoreRequest());

        // 주문 항목 업데이트: 기존 항목을 전부 제거하고 새로 추가
        if (dto.getItems() != null) {
            // 기존 주문 항목 전체 제거
            order.getOrderItems().clear();

            // 새 항목 리스트 생성 및 주문에 추가
            List<OrderItem> updatedItems = dto.getItems().stream()
                    .map(itemDto -> {
                        // itemId로 실제 상품 엔티티 조회
                        var item = itemRepository.findById(itemDto.getItemId())
                                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
                        // 새로운 OrderItem 생성
                        OrderItem orderItem = new OrderItem(itemDto, item);
                        orderItem.setOrder(order);
                        return orderItem;
                    })
                    .collect(Collectors.toList());
            order.getOrderItems().addAll(updatedItems);
        }

        // 변경된 주문 저장
        Order updatedOrder = orderRepository.save(order);
        return new OrderResponseDto(updatedOrder);
    }

    @Transactional
    public OrderResponseDto cancelOrder(UUID orderId, OrderCancelRequestDto dto, Long memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getRole() == Role.CUSTOMER) {
            if (!order.getMember().getMemberId().equals(memberId)) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED);
            }
            if (order.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.ORDER_TIME_OUT);
            }
        } else if (member.getRole() == Role.OWNER) {
            Store store = order.getStore();
            if (!store.getMember().getMemberId().equals(memberId)) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED);
            }
        }
        // 관리자/최고관리자: 전체 접근 허용

        order.cancelOrder();
        Order canceledOrder = orderRepository.save(order);
        return new OrderResponseDto(canceledOrder);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(UUID orderId, Long memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getRole() == Role.CUSTOMER) {
            if (!order.getMember().getMemberId().equals(memberId)) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED);
            }
        } else if (member.getRole() == Role.OWNER) {
            Store store = order.getStore();
            if (!store.getMember().getMemberId().equals(memberId)) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED);
            }
        }
        // 관리자나 최고관리자는 전체 주문 조회 가능

        return new OrderDetailResponseDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(Long memberId) {
        List<Order> orders = orderRepository.findByMemberMemberId(memberId);
        return orders.stream().map(OrderResponseDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getStoreOrders(UUID storeId, Long memberId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        if (!store.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        List<Order> orders = orderRepository.findByStoreStoreId(storeId);
        return orders.stream().map(OrderResponseDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> searchOrders(Long memberId, String storeName, String productName) {
        List<Order> orders = orderRepository.searchOrders(memberId, storeName, productName);
        return orders.stream().map(OrderResponseDto::new).collect(Collectors.toList());
    }
}
