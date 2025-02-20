package com.sparta.tl3p.backend.domain.item.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ItemCreateRequestDto {
    @NotNull(message = "가게 ID는 필수입니다")
    private UUID storeId;

    @NotBlank(message = "상품명은 필수입니다")
    @Size(min = 1, max = 100, message = "상품명은 1-100자 사이여야 합니다")
    private String itemName;

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 최소 0원 이상 이어야 합니다")
    @Max(value = 10000000, message = "범위를 벗어난 값 입니다")
    private BigDecimal price;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;
}