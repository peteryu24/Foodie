package com.sparta.tl3p.backend.domain.item.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.domain.item.enums.ItemStatus;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("status != 'DELETED'")
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id", columnDefinition = "uuid")
    private UUID itemId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ItemStatus status = ItemStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;


    @Builder
    public Item(Store store, String name, BigDecimal price, String description) {
        this.store = store;
        this.name = name;
        this.price = price;
        this.description = description;
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