package com.sparta.tl3p.backend.domain.store.repository;

import com.sparta.tl3p.backend.domain.store.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {

    List<StoreCategory> findByStoreId(UUID storeId);

    @Transactional
    @Modifying
    @Query("DELETE FROM StoreCategory sc WHERE sc.store.storeId = :storeId")
    void deleteByStoreId(UUID storeId);
}
