package com.sparta.tl3p.backend.domain.item.dto;

import com.sparta.tl3p.backend.domain.item.entity.ItemStatus;
import lombok.Data;

@Data
public class ItemSearchRequest {
    private String     keyword;
    private ItemStatus status;
    private Integer    page = 0;
    private Integer    size = 10;
}
