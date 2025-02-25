package com.sparta.tl3p.backend.domain.review.repository;

import com.sparta.tl3p.backend.domain.review.entity.Review;

import java.util.List;
import java.util.UUID;

public interface ReviewCustomRepository {

    List<Review> searchReviews(UUID storeId, String query);

    List<Review> searchOwnerReviews(UUID storeId, Long memberId);
}
