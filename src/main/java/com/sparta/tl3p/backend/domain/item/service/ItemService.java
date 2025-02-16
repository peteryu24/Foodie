package com.sparta.tl3p.backend.domain.item.service;

import com.sparta.tl3p.backend.domain.item.dto.ItemCreateRequest;
import com.sparta.tl3p.backend.domain.item.dto.ItemResponse;
import com.sparta.tl3p.backend.domain.item.dto.ItemUpdateRequest;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemResponse createItem(ItemCreateRequest request) {
        Item item = Item.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();

        return ItemResponse.of(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse updateItem(UUID id, ItemUpdateRequest request) {
        Item item = findItemById(id);
        log.info("Before update - updatedAt: {}", item.getUpdatedAt());

        item.updateItem(request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getItemStatus()
        );

        log.info("After update - updatedAt: {}", item.getUpdatedAt());
        return ItemResponse.of(item);
    }


    @Transactional
    public void deleteItem(UUID id) {
        Item item = findItemById(id);

        //TODO: 임시 코드
        item.softDelete(1L);
        itemRepository.save(item);
    }

    private Item findItemById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getItems() {
        return itemRepository.findAll().stream()
                .map(ItemResponse::of)
                .collect(Collectors.toList());
    }

}
