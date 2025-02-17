package com.sparta.tl3p.backend.domain.review.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_review")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long reviewId;

    @Column(nullable = false)
    private double score;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.CREATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private void updateStatus(ReviewStatus reviewStatus) {
        this.status = reviewStatus;
    }
}
