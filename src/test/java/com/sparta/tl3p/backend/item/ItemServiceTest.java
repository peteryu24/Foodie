package com.sparta.tl3p.backend.item;

import com.sparta.tl3p.backend.domain.item.dto.ItemCreateRequestDto;
import com.sparta.tl3p.backend.domain.item.dto.ItemResponseDto;
import com.sparta.tl3p.backend.domain.item.dto.ItemSearchRequestDto;
import com.sparta.tl3p.backend.domain.item.dto.ItemUpdateRequestDto;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.enums.ItemSortOption;
import com.sparta.tl3p.backend.domain.item.enums.ItemStatus;
import com.sparta.tl3p.backend.domain.item.repository.ItemRepository;
import com.sparta.tl3p.backend.domain.item.service.ItemService;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import com.sparta.tl3p.backend.domain.store.entity.Store;
import com.sparta.tl3p.backend.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StoreRepository storeRepository;

    private UUID   itemId;
    private Long   memberId;
    private Store  store;
    private Item   item;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();
        memberId = 1L;

        Member member = mock(Member.class);
        lenient().when(member.getMemberId()).thenReturn(memberId);
        lenient().when(member.getUsername()).thenReturn("testowner");
        lenient().when(member.getEmail()).thenReturn("testowner@gmail.com");

        store = mock(Store.class);
        lenient().when(store.getName()).thenReturn("teststore");
        lenient().when(store.getMember()).thenReturn(member);

        item = Item.builder()
                .store(store)
                .name("Test Item")
                .price(new BigDecimal("10000"))
                .description("Test Description")
                .build();
    }

    @Test
    @DisplayName("상품 등록 성공 테스트")
    void createItem_success() {
        // given
        ItemCreateRequestDto request = ItemCreateRequestDto.builder()
                .storeId(UUID.randomUUID())
                .itemName("New Item")
                .price(new BigDecimal("15000"))
                .description("New Description")
                .build();

        when(storeRepository.findById(request.getStoreId())).thenReturn(Optional.of(store));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // when
        ItemResponseDto response = itemService.createItem(request, memberId);

        // then
        assertThat(response).isNotNull();
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("상품 수정 성공 테스트")
    void updateItem_success() {
        // given
        ItemUpdateRequestDto request = ItemUpdateRequestDto.builder()
                .itemName("Updated Item")
                .price(new BigDecimal("99999"))
                .description("Updated Description")
                .status(ItemStatus.ACTIVE)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // when
        ItemResponseDto response = itemService.updateItem(itemId, request, memberId);

        // then
        assertThat(response.getItemName()).isEqualTo(request.getItemName());
        assertThat(response.getPrice()).isEqualTo(request.getPrice());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    @DisplayName("상품 상세 조회 성공 테스트")
    void getItem_success() {
        // given
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // when
        ItemResponseDto response = itemService.getItem(itemId);

        // then
        assertThat(response.getItemName()).isEqualTo(item.getName());
        assertThat(response.getPrice()).isEqualTo(item.getPrice());
        assertThat(response.getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    @DisplayName("상품 검색 성공 테스트")
    void searchItems_success() {
        // given
        ItemSearchRequestDto request = new ItemSearchRequestDto();
        request.setItemName("Test");
        request.setPage(0);
        request.setSize(10);
        request.setSortOption(ItemSortOption.CREATED_AT_DESC);

        Page<Item> itemPage = new PageImpl<>(List.of(item));
        when(itemRepository.findAllWithStore(request)).thenReturn(itemPage);

        // when
        Page<Item> result = itemService.getAllItems(request);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo(item.getName());
    }

    @Test
    @DisplayName("상품 숨김 처리 성공 테스트")
    void hideItem_success() {
        // given
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // when
        ItemResponseDto response = itemService.hideItem(itemId, memberId);

        // then
        assertThat(item.getStatus()).isEqualTo(ItemStatus.HIDDEN);
    }

    @Test
    @DisplayName("상품 삭제 성공 테스트")
    void deleteItem_success() {
        // given
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // when
        itemService.deleteItem(itemId, memberId);

        // then
        assertThat(item.getStatus()).isEqualTo(ItemStatus.DELETED);
    }
}
