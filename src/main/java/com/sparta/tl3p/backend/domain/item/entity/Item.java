package com.sparta.tl3p.backend.domain.item.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ItemStatus status = ItemStatus.ACTIVE;

    @Builder
    public Item(String name, BigDecimal price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public void updateItem(String name, BigDecimal price, String description, ItemStatus status) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.status = status;
    }

    public void hideItem() {
        this.status = ItemStatus.HIDDEN;
    }

    @Override
    public void softDelete(Long deleteUserId) {
        super.softDelete(deleteUserId);
        this.status = ItemStatus.DELETED;
    }
}
