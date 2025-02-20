package com.sparta.tl3p.backend.domain.review.repository;

import com.sparta.tl3p.backend.domain.review.entity.Review;
import com.sparta.tl3p.backend.domain.review.entity.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewCustomRepository {
    Optional<Review> findByIdAndNotInReviewStatus(UUID reviewId, ReviewStatus reviewStatus);
}
