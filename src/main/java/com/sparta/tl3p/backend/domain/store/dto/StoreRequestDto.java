package com.sparta.tl3p.backend.domain.store.dto;

import com.sparta.tl3p.backend.common.type.Address;
import com.sparta.tl3p.backend.domain.store.enums.CategoryType;
import com.sparta.tl3p.backend.domain.store.enums.StoreStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StoreRequestDto {

    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;

    private String content;

    @NotNull(message = "주소는 필수입니다.")
    private Address address;

    @NotNull(message = "가게 상태는 필수입니다.")
    private StoreStatus status;

    @NotNull(message = "카테고리는 필수입니다.")
    private List<CategoryType> categories;
}
