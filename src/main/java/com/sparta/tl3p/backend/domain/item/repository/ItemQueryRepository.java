package com.sparta.tl3p.backend.domain.item.repository;

import com.sparta.tl3p.backend.domain.item.dto.ItemSearchRequestDto;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import org.springframework.data.domain.Page;

public interface ItemQueryRepository {
    Page<Item> findAllWithStore(ItemSearchRequestDto request);
}
