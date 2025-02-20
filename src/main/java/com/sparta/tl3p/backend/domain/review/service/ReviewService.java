package com.sparta.tl3p.backend.domain.review.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.order.repository.OrderRepository;
import com.sparta.tl3p.backend.domain.review.dto.ReviewResponseDto;
import com.sparta.tl3p.backend.domain.review.entity.Review;
import com.sparta.tl3p.backend.domain.review.entity.ReviewStatus;
import com.sparta.tl3p.backend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void createReview(UUID orderId, String content, double score) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Review review = Review.createReview(content, score, order);
        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(UUID reviewId, String content, double score) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        review.updateReview(content, score);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> searchReviews(UUID storeId, String query) {
        List<Review> reviews = reviewRepository.searchReviews(storeId, query);
        return reviews.stream().map(ReviewResponseDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto findReview(UUID reviewId) {
        Review review = reviewRepository.findByIdAndNotInReviewStatus(reviewId, ReviewStatus.DELETED)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        return new ReviewResponseDto(review);
    }

    @Transactional
    public void hideReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if (review.getStatus() == ReviewStatus.DELETED) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_DELETED);
        }

        review.hideReview();
    }

    @Transactional
    public void deleteReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if (review.getStatus() == ReviewStatus.DELETED) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_DELETED);
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> searchOwnerReviews(UUID storeId, Long memberId) {
        List<Review> reviews = reviewRepository.searchOwnerReviews(storeId, memberId);
        return reviews.stream().map(ReviewResponseDto::new).collect(Collectors.toList());
    }
}
