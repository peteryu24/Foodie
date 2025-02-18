package com.sparta.tl3p.backend.domain.store.controller;

import com.sparta.tl3p.backend.common.dto.SuccessResponseDto;
import com.sparta.tl3p.backend.common.type.ResponseCode;
import com.sparta.tl3p.backend.domain.store.dto.StoreRequestDto;
import com.sparta.tl3p.backend.domain.store.dto.StoreResponseDto;
import com.sparta.tl3p.backend.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // 가게 상세 조회
    @GetMapping("/{storeId}")
    public ResponseEntity<SuccessResponseDto> getStore(@PathVariable UUID storeId) {
        StoreResponseDto response = storeService.getStore(storeId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("")
                        .data(response)
                        .build()
        );
    }

    // 가게 목록 검색
    @GetMapping
    public ResponseEntity<SuccessResponseDto> searchStores(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query) {
        List<StoreResponseDto> stores = storeService.searchStores(category, query);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(stores.isEmpty() ? ResponseCode.NS : ResponseCode.S)
                        .message("")
                        .data(stores)
                        .build()
        );
    }


    // 본인의 가게 목록 조회
    @GetMapping("/owner/stores")
    public ResponseEntity<SuccessResponseDto> getMyStores(@AuthenticationPrincipal Long ownerId) {
        List<StoreResponseDto> stores = storeService.getStoresByOwner(ownerId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("")
                        .data(stores)
                        .build()
        );
    }

    // 가게 정보 수정
    @PutMapping("/{storeId}")
    public ResponseEntity<SuccessResponseDto> updateStore(
            @PathVariable UUID storeId,
            @RequestBody @Valid StoreRequestDto requestDto,
            @AuthenticationPrincipal Long ownerId) {
        storeService.updateStore(storeId, requestDto, ownerId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("가게 정보 수정 완료.")
                        .data(null)
                        .build()
        );
    }

    // 가게 숨김
    @PatchMapping("/{storeId}")
    public ResponseEntity<SuccessResponseDto> hideStore(@PathVariable UUID storeId, @AuthenticationPrincipal Long ownerId) {
        storeService.hideStore(storeId, ownerId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("가게 숨김 완료.")
                        .data(null)
                        .build()
        );
    }

    // 가게 삭제
    @DeleteMapping("/{storeId}")
    public ResponseEntity<SuccessResponseDto> deleteStore(@PathVariable UUID storeId, @AuthenticationPrincipal Long ownerId) {
        storeService.deleteStore(storeId, ownerId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("가게가 삭제 완료.")
                        .data(null)
                        .build()
        );
    }

    // 가게 전체 리뷰 평점 조회
    @GetMapping("/{storeId}/scores")
    public ResponseEntity<SuccessResponseDto> getStoreReviewScore(@PathVariable UUID storeId) {
        double avgScore = storeService.getStoreReviewScore(storeId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("")
                        .data(avgScore)
                        .build()
        );
    }
}
