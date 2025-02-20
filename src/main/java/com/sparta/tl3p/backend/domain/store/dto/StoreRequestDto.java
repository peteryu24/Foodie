package com.sparta.tl3p.backend.domain.store.dto;

import com.sparta.tl3p.backend.common.type.Address;
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

    @NotNull(message = "가게 이름은 필수입니다.")
    private String name;

    private String content;

    @NotNull(message = "주소는 필수입니다.")
    private Address address;

    @NotNull(message = "가게 상태는 필수입니다.")
    private StoreStatus status;

    @NotNull(message = "가게 주인 ID는 필수입니다.")
    private Long memberId;

    private List<String> categoryIds;

    // ✅ 테스트용 생성자 추가
    public StoreRequestDto(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
