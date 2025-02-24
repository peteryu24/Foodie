package com.sparta.tl3p.backend.domain.item.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.item.dto.ItemCreateRequestDto;
import com.sparta.tl3p.backend.domain.item.dto.ItemResponseDto;
import com.sparta.tl3p.backend.domain.item.dto.ItemSearchRequestDto;
import com.sparta.tl3p.backend.domain.item.dto.ItemUpdateRequestDto;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.repository.ItemRepository;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.repository.StoreRepository;
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
    private final ItemRepository  itemRepository;
    private final StoreRepository storeRepository;

    public ItemResponseDto getItem(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        return ItemResponseDto.from(item);
    }

    @Transactional
    public ItemResponseDto createItem(ItemCreateRequestDto request, Long memberId) {

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        validateItemAccess(store.getMember(), memberId);

        Item item = Item.builder()
                .store(store)
                .name(request.getItemName())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();

        return ItemResponseDto.from(itemRepository.save(item));
    }

    @Transactional
    public ItemResponseDto updateItem(UUID id, ItemUpdateRequestDto request, Long memberId) {

        Item item = findItemById(id);
        validateItemAccess(item.getStore().getMember(), memberId);

        item.updateItem(
                request.getItemName(),
                request.getPrice(),
                request.getDescription(),
                request.getStatus()
        );

        return ItemResponseDto.from(item);
    }

    @Transactional
    public void deleteItem(UUID id, Long memberId) {
        Item item = findItemById(id);
        validateItemAccess(item.getStore().getMember(), memberId);

        item.softDelete(memberId);
    }

    public Page<Item> getAllItems(ItemSearchRequestDto request) {
        return itemRepository.findAllWithStore(request);
    }

    @Transactional
    public ItemResponseDto hideItem(UUID id, Long memberId) {
        Item item = findItemById(id);
        validateItemAccess(item.getStore().getMember(), memberId);
        item.hideItem();

        return ItemResponseDto.from(item);
    }


    private Item findItemById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
    }

    private void validateItemAccess(Member owner, Long memberId) {
        if (owner == null || memberId == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        if (!owner.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }
}
