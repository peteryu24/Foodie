package com.sparta.tl3p.backend.domain.item.controller;

import com.sparta.tl3p.backend.common.dto.SuccessResponseDto;
import com.sparta.tl3p.backend.common.type.ResponseCode;
import com.sparta.tl3p.backend.domain.item.dto.*;
import com.sparta.tl3p.backend.domain.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/items/v1")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<SuccessResponseDto> getAllItems(
            @ModelAttribute @Valid ItemSearchRequestDto request
    ) {
        Page<ItemResponseDto> result = itemService.getAllItems(request).map(ItemResponseDto::from);
        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 목록 조회 성공")
                .data(ItemPageResponseDto.of(result))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDto> getItem(@PathVariable UUID id) {
        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 상세 조회 성공")
                .data(itemService.getItem(id))
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SuccessResponseDto> createItem(
            @Valid @RequestBody ItemCreateRequestDto request
    ) {
        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 등록 성공")
                .data(itemService.createItem(request))
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SuccessResponseDto> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody ItemUpdateRequestDto request
    ) {
        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 수정 성공")
                .data(itemService.updateItem(id, request))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SuccessResponseDto> deleteItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 삭제 성공")
                .build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SuccessResponseDto> hideItem(@PathVariable UUID id) {
        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 숨김 성공")
                .data(itemService.hideItem(id))
                .build());
    }
}
