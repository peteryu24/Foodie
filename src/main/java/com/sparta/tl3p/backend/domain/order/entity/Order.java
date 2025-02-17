package com.sparta.tl3p.backend.domain.order.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.domain.order.dto.OrderRequestDto;
import com.sparta.tl3p.backend.domain.order.enums.DataStatus;
import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.payment.entity.Payment;
import com.sparta.tl3p.backend.domain.review.entity.Review;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "order_id")
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "store_request")
    private String storeRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DataStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    // Factory 메소드로 주문 생성 (DTO, 회원, 가게를 받아 처리)
    public static Order createOrder(com.sparta.tl3p.backend.domain.order.dto.OrderRequestDto dto, Member member, Store store, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .orderType(dto.getOrderType())
                .paymentMethod(dto.getPaymentMethod())
                .deliveryAddress(dto.getDeliveryAddress())
                .storeRequest(dto.getStoreRequest())
                .status(DataStatus.CREATED)
                .member(member)
                .store(store)
                .build();
        order.setOrderItems(orderItems);
        return order;
    }

    // 주문 아이템 전체 재등록 및 요청사항 업데이트
    public void updateOrderItems(List<OrderItem> newOrderItems, String storeRequest) {
        this.orderItems.clear();
        this.orderItems.addAll(newOrderItems);
        this.storeRequest = storeRequest;
        this.status = DataStatus.UPDATED;
    }

    // 주문 취소 처리
    public void cancelOrder() {
        this.status = DataStatus.DELETED;
    }
}
