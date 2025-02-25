package com.sparta.tl3p.backend.domain.ai.service;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import com.sparta.tl3p.backend.domain.ai.dto.AIDescriptionRequestDto;
import com.sparta.tl3p.backend.domain.ai.dto.AIDescriptionResponseDto;
import com.sparta.tl3p.backend.domain.ai.dto.GeminiApiRequestDto;
import com.sparta.tl3p.backend.domain.ai.dto.GeminiApiResponseDto;
import com.sparta.tl3p.backend.domain.ai.entity.AIDescription;
import com.sparta.tl3p.backend.domain.ai.repository.AIDescriptionRepository;
import com.sparta.tl3p.backend.domain.item.entity.Item;
import com.sparta.tl3p.backend.domain.item.repository.ItemRepository;
import com.sparta.tl3p.backend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class AIDescriptionService {

    @Value("${api.gemini.key}")
    private String GEMINI_API_KEY;

    @Value("${api.gemini.url}")
    private String GEMINI_API_URL;

    private final AIDescriptionRepository aiDescriptionRepository;
    private final ItemRepository          itemRepository;
    private final RestClient              restClient;

    @Transactional
    public AIDescriptionResponseDto generateDescription(AIDescriptionRequestDto request, Long memberId) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        validateAuthority(item.getStore().getMember(), memberId);

        String prompt     = generatePrompt(item);
        String aiResponse = callAIApi(prompt);

        AIDescription aiDescription = AIDescription.builder()
                .prompt(prompt)
                .response(aiResponse)
                .item(item)
                .member(item.getStore().getMember())
                .build();

        aiDescriptionRepository.save(aiDescription);

        updateItemDescription(item, aiResponse);

        return AIDescriptionResponseDto.from(aiDescription);
    }

    private void validateAuthority(Member member, Long memberId) {
        if (member == null || memberId == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        if (!member.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    private void updateItemDescription(Item item, String apiResult) {
        item.updateDescription(apiResult);
        itemRepository.save(item);
    }

    private String callAIApi(String prompt) {
        URI url = UriComponentsBuilder.fromUriString(GEMINI_API_URL)
                .queryParam("key", GEMINI_API_KEY)
                .build()
                .toUri();

        GeminiApiRequestDto geminiApiRequest = GeminiApiRequestDto.from(prompt);

        try {
            GeminiApiResponseDto response = restClient.post()
                    .uri(url)
                    .body(geminiApiRequest)
                    .retrieve()
                    .body(GeminiApiResponseDto.class);

            if (response == null) {
                throw new BusinessException(ErrorCode.API_STATUS_ERROR);
            }
            return response.extractText();
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.API_UNEXPECTED_ERROR);
        }
    }

    private String generatePrompt(Item item) {
        return String.format(
                """
                다음은 음식점 메뉴 상품 정보이다:
                상품명: %s
                가게이름: %s
                
                음식 배송 서비스에 제품을 등록하기 위해 해당 메뉴에 대한 실제 입력할 설명을
                가게이름과 상품이름의 특색을 살려 맛, 재료, 특징 등을 표현한 답변을 50자 이하로 작성.
                """,
                item.getName(),
                item.getStore().getName()
        );
    }
}