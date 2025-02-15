package com.sparta.tl3p.backend.domain.store.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreStatus {

    CREATED("생성됨"),
    UPDATED("수정됨"),
    DELETED("삭제됨");

    private final String description;
}
