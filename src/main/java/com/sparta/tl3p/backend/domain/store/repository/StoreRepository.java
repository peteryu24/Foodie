package com.sparta.tl3p.backend.domain.store.repository;

import com.sparta.tl3p.backend.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    Optional<Store> findByIdAndOwnerId(UUID storeId, Long ownerId);

    List<Store> findByOwnerId(Long ownerId);

    @Query("SELECT s FROM Store s WHERE " +
            "(:category IS NULL OR EXISTS (SELECT sc FROM StoreCategory sc WHERE sc.store = s AND sc.categoryId = :category)) " +
            "AND (:query IS NULL OR s.name LIKE %:query%)")
    List<Store> findStoresByCategoryAndQuery(String category, String query);

    @Query("SELECT COALESCE(AVG(r.score), 0) FROM Review r WHERE r.store.id = :storeId")
    double findAvgReviewScoreByStoreId(UUID storeId);
}
