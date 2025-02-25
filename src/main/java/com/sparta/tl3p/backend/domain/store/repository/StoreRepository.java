package com.sparta.tl3p.backend.domain.store.repository;

import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    List<Store> findByMemberMemberId(Long memberId);

    @Query("SELECT s FROM Store s LEFT JOIN s.storeCategories sc " +
            "WHERE (:category IS NULL OR sc.category = :category) " +
            "AND (:query IS NULL OR s.name LIKE %:query%)")
    List<Store> findStoresByCategoryAndQuery(CategoryType category, String query);

    @Query("SELECT COALESCE(AVG(r.score), 0) FROM Review r WHERE r.store.storeId = :storeId")
    double findAvgReviewScoreByStoreId(UUID storeId);

    @Query("SELECT s FROM Store s WHERE s.storeId = :storeId AND s.status != 'DELETED'")
    Optional<Store> findByIdExcludeDeleted(UUID storeId);
}
