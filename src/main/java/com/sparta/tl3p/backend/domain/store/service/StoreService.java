package com.sparta.tl3p.backend.domain.store.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.member.repository.MemberRepository;
import com.sparta.tl3p.backend.domain.store.dto.StoreRequestDto;
import com.sparta.tl3p.backend.domain.store.dto.StoreResponseDto;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.entity.StoreCategory;
import com.sparta.tl3p.backend.domain.store.enums.CategoryType;
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
    private final StoreCategoryRepository storeCategoryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public StoreResponseDto createStore(StoreRequestDto requestDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (requestDto.getCategories() == null || requestDto.getCategories().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_CATEGORY);
        }

        Store store = Store.builder()
                .name(requestDto.getName())
                .content(requestDto.getContent())
                .address(requestDto.getAddress())
                .member(member)
                .build();

        storeRepository.save(store);

        List<StoreCategory> categories = requestDto.getCategories().stream()
                .map(category -> new StoreCategory(store, CategoryType.valueOf(category.name())))
                .collect(Collectors.toList());
        storeCategoryRepository.saveAll(categories);

        return new StoreResponseDto(store);
    }

    public StoreResponseDto getStore(UUID storeId) {
        Store store = storeRepository.findByIdExcludeDeleted(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        double avgScore = storeRepository.findAvgReviewScoreByStoreId(storeId);
        return new StoreResponseDto(store, avgScore);
    }

    public List<StoreResponseDto> searchStores(String category, String query) {
        CategoryType categoryType = (category != null) ? CategoryType.valueOf(category.toUpperCase()) : null;
        List<Store> stores = storeRepository.findStoresByCategoryAndQuery(categoryType, query);
        return stores.stream().map(StoreResponseDto::new).collect(Collectors.toList());
    }

    public List<StoreResponseDto> getStoresByOwner(Long memberId) {
        List<Store> stores = storeRepository.findByMemberMemberId(memberId);
        return stores.stream().map(StoreResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void updateStore(UUID storeId, StoreRequestDto requestDto, Long memberId) {
        Store store = storeRepository.findByIdExcludeDeleted(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        store.updateStore(requestDto.getName(), requestDto.getContent(), requestDto.getAddress(), requestDto.getStatus());

        storeCategoryRepository.deleteByStoreId(storeId);

        List<StoreCategory> newCategories = requestDto.getCategories().stream()
                .map(category -> new StoreCategory(store, CategoryType.valueOf(category.name())))
                .collect(Collectors.toList());
        storeCategoryRepository.saveAll(newCategories);
    }

    @Transactional
    public void hideStore(UUID storeId, Long memberId) {
        Store store = storeRepository.findByIdExcludeDeleted(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        if (!store.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACTION);
        }
        store.hideStore();
    }

    @Transactional
    public void deleteStore(UUID storeId, Long memberId) {
        Store store = storeRepository.findByIdExcludeDeleted(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        if (!store.getMember().getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACTION);
        }
        store.softDelete(memberId);
    }

    public double getStoreReviewScore(UUID storeId) {
        return storeRepository.findAvgReviewScoreByStoreId(storeId);
    }
}
