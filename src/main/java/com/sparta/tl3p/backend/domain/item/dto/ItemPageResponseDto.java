package com.sparta.tl3p.backend.domain.item.dto;

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
    private final boolean               isFirst;
    private final boolean               isLast;
    private final boolean               hasNext;
    private final boolean               hasPrev;

    public static ItemPageResponseDto of(Page<ItemResponseDto> page) {
        return ItemPageResponseDto.builder()
                .items(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrev(page.hasPrevious())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}
