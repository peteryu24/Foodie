package com.sparta.tl3p.backend.domain.store.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreStatus {
    CREATED("운영 중"),
    UPDATED("수정됨"),
    DELETED("삭제됨");

    private final String description;
}