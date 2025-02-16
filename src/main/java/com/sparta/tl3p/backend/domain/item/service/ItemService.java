package com.sparta.tl3p.backend.domain.item.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.item.dto.ItemCreateRequest;
import com.sparta.tl3p.backend.domain.item.dto.ItemResponse;
import com.sparta.tl3p.backend.domain.item.dto.ItemUpdateRequest;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.repository.ItemRepository;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository  itemRepository;
    private final StoreRepository storeRepository;

    public ItemResponse getItem(UUID itemId) {
        return ItemResponse.from(
                itemRepository.findById(itemId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND))
        );
    }

    //    public Page<ItemResponse> searchItems(ItemSearchRequest request) {
    //        return itemRepository.search(request)
    //                .map(ItemResponse::from);
    //    }

    @Transactional
    public ItemResponse createItem(ItemCreateRequest request) {

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Item item = Item.builder()
                .store(store)
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();

        return ItemResponse.from(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse updateItem(UUID id, ItemUpdateRequest request) {
        Item item = findItemById(id);

        item.updateItem(request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getItemStatus()
        );

        return ItemResponse.from(item);
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

    @Transactional(readOnly = true)
    public List<ItemResponse> getItems() {
        return itemRepository.findAll().stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

}
