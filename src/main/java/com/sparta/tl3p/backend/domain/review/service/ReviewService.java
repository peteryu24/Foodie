package com.sparta.tl3p.backend.domain.review.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.order.entity.Order;
import com.sparta.tl3p.backend.domain.review.dto.ReviewResponseDto;
import com.sparta.tl3p.backend.domain.review.entity.Review;
import com.sparta.tl3p.backend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
//    private final OrderRepository orderRepository;

    @Transactional
    public void createReview(UUID orderId, String content, double score) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        Review review = new Review();
        // todo: 더미 데이터 제거
        review.createReview(content, score, new Order());
        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(UUID reviewId, String content, double score) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        review.updateReview(content, score);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> searchReviews(UUID storeId, String product) {
        // todo: QueryDSL 적용 및 비즈니스 로직 구현
        return null;
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto searchReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        return new ReviewResponseDto(review);
    }

    @Transactional
    public void hideReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        review.hideReview();
    }

    @Transactional
    public void deleteReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
    }
}
