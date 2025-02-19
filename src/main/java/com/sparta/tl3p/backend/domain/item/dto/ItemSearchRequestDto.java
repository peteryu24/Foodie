package com.sparta.tl3p.backend.domain.item.dto;

import com.sparta.tl3p.backend.domain.item.enums.ItemSortOption;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchRequestDto {
    @NotNull(message = "가게 ID는 필수입니다")
    private UUID    storeId;

    @Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다")
    private String  itemName;

    @PositiveOrZero
    private Integer minPrice;

    @PositiveOrZero
    private Integer maxPrice;

    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private ItemSortOption sortOption = ItemSortOption.CREATED_AT_DESC;

    public int getPage() {
        return (page != null && page > 0) ? page - 1 : 0;
    }

    public int getSize() {
        return Optional.ofNullable(size)
                .filter(s -> Set.of(10, 30, 50).contains(s))
                .orElse(10);
    }
}