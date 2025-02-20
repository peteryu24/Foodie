package com.sparta.tl3p.backend.domain.order.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.repository.ItemRepository;
import com.sparta.tl3p.backend.domain.member.entity.Member;
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
                Item item = itemRepository.findById(itemDto.getItemId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
                totalAmount = totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            }
        }

        Order order = new Order(dto, member, store);
        order.setCreatedAt(LocalDateTime.now());

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<OrderItem> orderItems = dto.getItems().stream().map(itemDto -> {
                Item item = itemRepository.findById(itemDto.getItemId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
                return new OrderItem(itemDto, item);
            }).collect(Collectors.toList());
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
        if (!order.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        order.updateOrder(dto);
        Order updatedOrder = orderRepository.save(order);
        return new OrderResponseDto(updatedOrder);
    }

    @Transactional
    public OrderResponseDto cancelOrder(UUID orderId, OrderCancelRequestDto dto, Long memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        order.cancelOrder();
        Order canceledOrder = orderRepository.save(order);
        return new OrderResponseDto(canceledOrder);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(UUID orderId, Long memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
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
