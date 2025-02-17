package com.sparta.tl3p.backend.domain.item.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.item.dto.ItemCreateRequestDto;
import com.sparta.tl3p.backend.domain.item.dto.ItemResponseDto;
import com.sparta.tl3p.backend.domain.item.dto.ItemSearchRequestDto;
import com.sparta.tl3p.backend.domain.item.dto.ItemUpdateRequestDto;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    //    private final StoreRepository storeRepository;

    public ItemResponseDto getItem(UUID itemId) {
        return ItemResponseDto.of(
                itemRepository.findById(itemId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND))
        );
    }

    @Transactional
    public ItemResponseDto createItem(ItemCreateRequestDto request) {

        //        Store store = storeRepository.findById(request.getStoreId())
        //                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Item item = Item.builder()
                //                .store(store)
                .name(request.getItemName())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();

        return ItemResponseDto.of(itemRepository.save(item));
    }

    @Transactional
    public ItemResponseDto updateItem(UUID id, ItemUpdateRequestDto request) {
        Item item = findItemById(id);

        item.updateItem(request.getItemName(),
                request.getPrice(),
                request.getDescription(),
                request.getStatus()
        );

        return ItemResponseDto.of(item);
    }


    @Transactional
    public void deleteItem(UUID id) {
        Item item = findItemById(id);

        //TODO: 임시 코드
        item.softDelete(1L);
    }

    private Item findItemById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
    }

    public Page<Item> getAllItems(ItemSearchRequestDto request) {
        return itemRepository.findAllWithStore(request);
    }

    @Transactional
    public ItemResponseDto hideItem(UUID id) {
        Item item = findItemById(id);
        item.hideItem();

        return ItemResponseDto.of(item);
    }
}
