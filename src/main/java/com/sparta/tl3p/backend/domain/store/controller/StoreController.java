package com.sparta.tl3p.backend.domain.store.controller;

import com.sparta.tl3p.backend.common.dto.SuccessResponseDto;
import com.sparta.tl3p.backend.common.type.ResponseCode;
import com.sparta.tl3p.backend.domain.member.entity.CustomUserDetails;
import com.sparta.tl3p.backend.domain.store.dto.StoreRequestDto;
import com.sparta.tl3p.backend.domain.store.dto.StoreResponseDto;
import com.sparta.tl3p.backend.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<SuccessResponseDto> createStore(
            @RequestBody @Valid StoreRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        StoreResponseDto response = storeService.createStore(requestDto, memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("가게 등록 완료.")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<SuccessResponseDto> getStore(@PathVariable UUID storeId) {
        StoreResponseDto response = storeService.getStore(storeId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("가게 조회 완료.")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<SuccessResponseDto> searchStores(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query) {
        List<StoreResponseDto> stores = storeService.searchStores(category, query);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(stores.isEmpty() ? ResponseCode.NS : ResponseCode.S)
                        .message("가게 검색 완료.")
                        .data(stores)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owner/stores")
    public ResponseEntity<SuccessResponseDto> getMyStores(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<StoreResponseDto> stores = storeService.getStoresByOwner(memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("본인 가게 조회 완료.")
                        .data(stores)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{storeId}")
    public ResponseEntity<SuccessResponseDto> updateStore(
            @PathVariable UUID storeId,
            @RequestBody @Valid StoreRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        storeService.updateStore(storeId, requestDto, memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("가게 정보 수정 완료.")
                        .data(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{storeId}")
    public ResponseEntity<SuccessResponseDto> hideStore(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        storeService.hideStore(storeId, memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("가게 숨김 완료.")
                        .data(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<SuccessResponseDto> deleteStore(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        storeService.deleteStore(storeId, memberId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("가게 삭제 완료.")
                        .data(null)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/{storeId}/scores")
    public ResponseEntity<SuccessResponseDto> getStoreReviewScore(@PathVariable UUID storeId) {
        double avgScore = storeService.getStoreReviewScore(storeId);
        return ResponseEntity.ok(
                SuccessResponseDto.builder()
                        .code(ResponseCode.S)
                        .message("가게 평점 조회 완료.")
                        .data(avgScore)
                        .build()
        );
    }
}
