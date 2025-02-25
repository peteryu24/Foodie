package com.sparta.tl3p.backend.store;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.Address;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.store.dto.StoreRequestDto;
import com.sparta.tl3p.backend.domain.store.dto.StoreResponseDto;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.entity.StoreCategory;
import com.sparta.tl3p.backend.domain.store.enums.CategoryType;
import com.sparta.tl3p.backend.domain.store.enums.StoreStatus;
import com.sparta.tl3p.backend.domain.store.repository.StoreCategoryRepository;
import com.sparta.tl3p.backend.domain.store.repository.StoreRepository;
import com.sparta.tl3p.backend.domain.store.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreCategoryRepository storeCategoryRepository;

    @InjectMocks
    private StoreService storeService;

    private Store store;
    private Member owner;
    private Address address;
    private UUID storeId;

    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID();

        owner = mock(Member.class);
        lenient().when(owner.getMemberId()).thenReturn(1L);

        address = mock(Address.class);
        lenient().when(address.getCity()).thenReturn("Seoul");

        store = Store.builder()
                .name("Test Store")
                .content("A test store description")
                .address(address)
                .member(owner)
                .build();
    }

    @Test
    void returnStoreDetails_whenStoreExists() {
        // given
        when(storeRepository.findByIdExcludeDeleted(storeId)).thenReturn(Optional.of(store));
        when(storeRepository.findAvgReviewScoreByStoreId(storeId)).thenReturn(4.5);

        // when
        StoreResponseDto response = storeService.getStore(storeId);

        // then
        assertThat(response.getId()).isEqualTo(store.getStoreId());
        assertThat(response.getName()).isEqualTo(store.getName());
        assertThat(response.getAvgScore()).isEqualTo(4.5);
    }

    @Test
    void throwException_whenStoreNotFound() {
        // given
        when(storeRepository.findByIdExcludeDeleted(storeId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeService.getStore(storeId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.STORE_NOT_FOUND.getMessage());
    }

    @Test
    void returnStores_whenSearchMatches() {
        // given
        List<Store> stores = Collections.singletonList(store);
        when(storeRepository.findStoresByCategoryAndQuery(CategoryType.CAFE, "Test"))
                .thenReturn(stores);

        // when
        List<StoreResponseDto> result = storeService.searchStores("CAFE", "Test");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Store");
    }

    @Test
    void returnStoresByOwner() {
        // given
        when(storeRepository.findByMemberMemberId(1L)).thenReturn(Collections.singletonList(store));

        // when
        List<StoreResponseDto> result = storeService.getStoresByOwner(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Store");
    }

    @Test
    void updateStoreTest() {
        // given
        StoreRequestDto requestDto = mock(StoreRequestDto.class);
        when(storeRepository.findByIdExcludeDeleted(storeId)).thenReturn(Optional.of(store));
        lenient().when(requestDto.getName()).thenReturn("Updated Store");
        lenient().when(requestDto.getContent()).thenReturn("Updated content");
        lenient().when(requestDto.getAddress()).thenReturn(address);
        lenient().when(requestDto.getStatus()).thenReturn(StoreStatus.UPDATED);
        lenient().when(requestDto.getCategories()).thenReturn(Arrays.asList(CategoryType.CHICKEN, CategoryType.CAFE));

        // when
        storeService.updateStore(storeId, requestDto, 1L);

        // then
        assertThat(store.getName()).isEqualTo("Updated Store");
        assertThat(store.getContent()).isEqualTo("Updated content");
        assertThat(store.getStatus()).isEqualTo(StoreStatus.UPDATED);
        verify(storeCategoryRepository).deleteByStoreId(storeId);
        verify(storeCategoryRepository).saveAll(any());
    }

    @Test
    void throwException_whenUnauthorizedUserTriesToUpdateStore() {
        // given
        when(storeRepository.findByIdExcludeDeleted(storeId)).thenReturn(Optional.of(store));

        // when & then
        assertThatThrownBy(() -> storeService.updateStore(storeId, mock(StoreRequestDto.class), 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED_ACTION.getMessage());
    }

    @Test
    void hideStoreTest() {
        // given
        when(storeRepository.findByIdExcludeDeleted(storeId)).thenReturn(Optional.of(store));

        // when
        storeService.hideStore(storeId, 1L);

        // then
        assertThat(store.getStatus()).isEqualTo(StoreStatus.DELETED);
    }

    @Test
    void throwException_whenUnauthorizedUserTriesToHideStore() {
        // given
        when(storeRepository.findByIdExcludeDeleted(storeId)).thenReturn(Optional.of(store));

        // when & then
        assertThatThrownBy(() -> storeService.hideStore(storeId, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED_ACTION.getMessage());
    }

    @Test
    void deleteStoreTest() {
        // given
        when(storeRepository.findByIdExcludeDeleted(storeId)).thenReturn(Optional.of(store));

        // when
        storeService.deleteStore(storeId, 1L);

        // then
        verify(storeRepository).findByIdExcludeDeleted(storeId);
    }

    @Test
    void throwException_whenUnauthorizedUserTriesToDeleteStore() {
        // given
        when(storeRepository.findByIdExcludeDeleted(storeId)).thenReturn(Optional.of(store));

        // when & then
        assertThatThrownBy(() -> storeService.deleteStore(storeId, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED_ACTION.getMessage());
    }

    @Test
    void returnReviewScoreForStore() {
        // given
        when(storeRepository.findAvgReviewScoreByStoreId(storeId)).thenReturn(4.2);

        // when
        double score = storeService.getStoreReviewScore(storeId);

        // then
        assertThat(score).isEqualTo(4.2);
    }
}
