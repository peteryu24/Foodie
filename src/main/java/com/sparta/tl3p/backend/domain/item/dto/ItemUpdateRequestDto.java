package com.sparta.tl3p.backend.domain.item.dto;

import com.sparta.tl3p.backend.domain.item.enums.ItemStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ItemUpdateRequestDto {
    @Size(min = 1, max = 100, message = "상품명은 1-100자 사이여야 합니다")
    private String itemName;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
    @Max(value = 10000000, message = "범위를 벗어난 값입니다")
    private BigDecimal price;

    private ItemStatus status;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;
}
