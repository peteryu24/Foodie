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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','OWNER','MANAGER','MASTER')")
    public ResponseEntity<SuccessResponseDto> getAllItems(
            @ModelAttribute @Valid ItemSearchRequestDto request
    ) {
        Page<ItemResponseDto> result = itemService.getAllItems(request).map(ItemResponseDto::from);
        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 목록 조회 성공")
                .data(ItemPageResponseDto.of(result, request.getSortOption()))
                .build());
    }

    @GetMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','OWNER','MANAGER','MASTER')")
    public ResponseEntity<SuccessResponseDto> getItem(
            @PathVariable UUID itemId
    ) {
        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 상세 조회 성공")
                .data(itemService.getItem(itemId))
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SuccessResponseDto> createItem(
            @Valid @RequestBody ItemCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = Long.valueOf(userDetails.getUsername());

        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 등록 성공")
                .data(itemService.createItem(request, memberId))
                .build());
    }

    @PutMapping("/{itemId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SuccessResponseDto> updateItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody ItemUpdateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = Long.valueOf(userDetails.getUsername());

        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 수정 성공")
                .data(itemService.updateItem(itemId, request, memberId))
                .build());
    }

    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SuccessResponseDto> deleteItem(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = Long.valueOf(userDetails.getUsername());

        itemService.deleteItem(itemId, memberId);
        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 삭제 성공")
                .build());
    }

    @PatchMapping("/{itemId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<SuccessResponseDto> hideItem(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long memberId = Long.valueOf(userDetails.getUsername());

        return ResponseEntity.ok(SuccessResponseDto.builder()
                .code(ResponseCode.S)
                .message("상품 숨김 성공")
                .data(itemService.hideItem(itemId, memberId))
                .build());
    }
}