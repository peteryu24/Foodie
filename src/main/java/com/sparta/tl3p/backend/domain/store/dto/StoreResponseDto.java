package com.sparta.tl3p.backend.domain.store.dto;

import com.sparta.tl3p.backend.common.type.Address;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.entity.StoreCategory;
import com.sparta.tl3p.backend.domain.store.enums.CategoryType;
import com.sparta.tl3p.backend.domain.store.enums.StoreStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class StoreResponseDto {

    private UUID id;
    private String name;
    private String content;
    private Address address;
    private StoreStatus status;
    private Long memberId;
    private List<CategoryType> categories;
    private double avgScore;

    public StoreResponseDto(Store store) {
        this.id = store.getStoreId();
        this.name = store.getName();
        this.content = store.getContent();
        this.address = store.getAddress();
        this.status = store.getStatus();
        this.memberId = store.getMember().getMemberId();
        this.categories = store.getStoreCategories().stream()
                .map(StoreCategory::getCategory)
                .collect(Collectors.toList());
        this.avgScore = 0.0;
    }

    public StoreResponseDto(Store store, Double avgScore) {
        this(store);
        this.avgScore = (avgScore != null) ? avgScore : 0.0;
    }
}
