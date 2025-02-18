package com.sparta.tl3p.backend.domain.store.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.store.dto.StoreRequestDto;
import com.sparta.tl3p.backend.domain.store.dto.StoreResponseDto;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.entity.StoreCategory;
import com.sparta.tl3p.backend.domain.store.repository.StoreCategoryRepository;
import com.sparta.tl3p.backend.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository; // 추가

    public StoreResponseDto getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        double avgScore = storeRepository.findAvgReviewScoreByStoreId(storeId);
        return new StoreResponseDto(store, avgScore);
    }

    public List<StoreResponseDto> searchStores(String category, String query) {
        List<Store> stores = storeRepository.findStoresByCategoryAndQuery(category, query);
        return stores.stream().map(StoreResponseDto::new).collect(Collectors.toList());
    }

    public List<StoreResponseDto> getStoresByOwner(Long memberId) {
        List<Store> stores = storeRepository.findByMemberMemberId(memberId);
        return stores.stream().map(StoreResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void updateStore(UUID storeId, StoreRequestDto requestDto, Long memberId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        store.updateStore(requestDto.getName(), requestDto.getContent(), requestDto.getAddress(), requestDto.getStatus());


        storeCategoryRepository.deleteByStoreId(storeId);
        List<StoreCategory> newCategories = requestDto.getCategoryIds().stream()
                .map(categoryId -> new StoreCategory(store, categoryId))
                .collect(Collectors.toList());
        storeCategoryRepository.saveAll(newCategories);
    }

    @Transactional
    public void hideStore(UUID storeId, Long memberId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        store.hideStore();
    }

    @Transactional
    public void deleteStore(UUID storeId, Long memberId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        store.softDelete(memberId);
    }

    public double getStoreReviewScore(UUID storeId) {
        return storeRepository.findAvgReviewScoreByStoreId(storeId);
    }
}
