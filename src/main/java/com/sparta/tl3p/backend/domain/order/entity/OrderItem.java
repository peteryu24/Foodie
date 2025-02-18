package com.sparta.tl3p.backend.domain.order.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.order.dto.OrderItemRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "order_item_id")
    private UUID orderItemId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 주문 아이템 생성 – OrderItemRequestDto와 Item을 이용
    public static OrderItem createOrderItem(OrderItemRequestDto dto, Item item) {
        return OrderItem.builder()
                .orderItemId(UUID.randomUUID())
                .item(item)
                .quantity(dto.getQuantity())
                .price(item.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                .build();
    }
}
