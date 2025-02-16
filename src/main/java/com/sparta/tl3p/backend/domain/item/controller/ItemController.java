package com.sparta.tl3p.backend.domain.item.controller;

import com.sparta.tl3p.backend.domain.item.dto.ItemCreateRequest;
import com.sparta.tl3p.backend.domain.item.dto.ItemResponse;
import com.sparta.tl3p.backend.domain.item.dto.ItemUpdateRequest;
import com.sparta.tl3p.backend.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/items/v1")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItems());
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(
            @RequestBody ItemCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.createItem(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable UUID id,
            @RequestBody ItemUpdateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.updateItem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
