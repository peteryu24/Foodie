package com.sparta.tl3p.backend.domain.item.dto;

import com.sparta.tl3p.backend.domain.item.enums.ItemSortOption;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class ItemPageResponseDto {
    private final List<ItemResponseDto> items;
    private final int                   totalPages;
    private final long                  totalElements;
    private final int                   pageSize;
    private final int                   currentPage;
    private final boolean               isFirst;
    private final boolean               isLast;
    private final boolean               hasNext;
    private final boolean               hasPrevious;
    private final ItemSortOption        sortOption;

    public static ItemPageResponseDto of(Page<ItemResponseDto> page, ItemSortOption sortOption) {
        return ItemPageResponseDto.builder()
                .items(page.getContent())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .currentPage(page.getNumber()+1)
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .sortOption(sortOption)
                .build();
    }
}