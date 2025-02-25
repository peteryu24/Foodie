package com.sparta.tl3p.backend.domain.order.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.common.type.Address;
import com.sparta.tl3p.backend.domain.order.dto.OrderRequestDto;
import com.sparta.tl3p.backend.domain.order.dto.OrderUpdateRequestDto;
import com.sparta.tl3p.backend.domain.order.enums.DataStatus;
import com.sparta.tl3p.backend.domain.order.enums.OrderType;
import com.sparta.tl3p.backend.domain.order.enums.PaymentMethod;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.payment.entity.Payment;
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
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Embedded
    @Column(name = "delivery_address")
    private Address deliveryAddress;

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

    // 생성자에서는 orderId를 직접 할당하지 않음.
    public Order(OrderRequestDto dto, Member member, Store store) {
        this.orderType = dto.getOrderType();
        this.paymentMethod = dto.getPaymentMethod();
        this.deliveryAddress = dto.getDeliveryAddress();
        this.storeRequest = dto.getStoreRequest();
        this.status = DataStatus.CREATED;
        this.member = member;
        this.store = store;
        this.orderItems = new ArrayList<>();
    }

    // 주문 수정 – storeRequest(및 필요시 주문 아이템 갱신) 처리
    public void updateOrder(OrderUpdateRequestDto dto) {
        this.storeRequest = dto.getStoreRequest();
        this.status = DataStatus.UPDATED;
    }

    // 주문 취소 처리
    public void cancelOrder() {
        this.status = DataStatus.DELETED;
    }
}
