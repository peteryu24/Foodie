package com.sparta.tl3p.backend.domain.review.dto;

import com.sparta.tl3p.backend.domain.item.enums.ItemStatus;
import com.sparta.tl3p.backend.domain.order.entity.OrderItem;
import com.sparta.tl3p.backend.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ReviewResponseDto {
    private final UUID reviewId;
    private final String nickname;
    private final String storeName;
    private final String content;
    private final double score;
    private final List<ReviewItemResponseDto> items;

    @Builder
    public ReviewResponseDto(Review review) {
        this.reviewId = review.getReviewId();
        this.nickname = review.getOrder().getMember().getNickname();
        this.storeName = review.getStore().getName();
        this.content = review.getContent();
        this.score = review.getScore();

        this.items = new ArrayList<>();
        for (OrderItem orderItem : review.getOrder().getOrderItems()) {
            if (orderItem.getItem().getStatus() == ItemStatus.ACTIVE) {
                this.items.add(ReviewItemResponseDto.of(orderItem.getItem()));
            }
        }
    }

}
