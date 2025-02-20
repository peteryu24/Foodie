package com.sparta.tl3p.backend.domain.review.entity;

import com.sparta.tl3p.backend.common.audit.BaseEntity;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_review")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reviewId;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.CREATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public void hideReview() {
        this.status = ReviewStatus.DELETED;
    }

    public void updateReview(String content, Double score) {
        this.content = content;
        this.score = score;
        this.status = ReviewStatus.UPDATED;
    }

    public static Review createReview(String content, Double score, Order order) {
        Review review = Review.builder()
                .content(content)
                .score(score)
                .order(order)
                .build();
        review.store = order.getStore();
//        order.setReview(review);  // 양방향 연관관계 처리
        return review;
    }

    @Builder
    private Review(String content, Double score, Order order) {
        this.content = content;
        this.score = score;
        this.order = order;
    }
}
