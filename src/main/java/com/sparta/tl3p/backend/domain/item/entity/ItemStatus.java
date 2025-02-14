package com.sparta.tl3p.backend.domain.item.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemStatus {
    ACTIVE("구매 가능"),
    DELETED("삭제됨"),
    HIDDEN("판매 불가")
    ;

    private final String description;
}
