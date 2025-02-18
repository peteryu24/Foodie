package com.sparta.tl3p.backend.domain.store.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
    KOREAN("한식"),
    CHICKEN("치킨"),
    JAPANESE("일식"),
    CHINESE("중식"),
    CAFE("카페"),
    PIZZA("피자"),
    ETC("기타");

    private final String description;
}
