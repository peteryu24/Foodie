package com.sparta.tl3p.backend.domain.store.dto;

import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.entity.StoreCategory;
import com.sparta.tl3p.backend.domain.store.enums.StoreStatus;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class StoreResponseDto {

    private UUID id;
    private String name;
    private String content;
    private String address;
    private StoreStatus status;
    private Long memberId;
    private List<String> categoryIds;
    private double avgScore;

    public StoreResponseDto(Store store) {
        this.id = store.getId();
        this.name = store.getName();
        this.content = store.getContent();
        this.address = store.getAddress();
        this.status = store.getStatus();
        this.memberId = store.getMember().getMemberId();
        this.categoryIds = store.getStoreCategories().stream()
                .map(StoreCategory::getCategoryId)
                .collect(Collectors.toList());
        this.avgScore = avgScore;
    }
    public StoreResponseDto(Store store, double avgScore) {
        this(store);
        this.avgScore = avgScore;
    }
}
