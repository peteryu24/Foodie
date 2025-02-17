package com.sparta.tl3p.backend.domain.ai.service;

import com.sparta.tl3p.backend.domain.ai.dto.AIDescriptionRequest;
import com.sparta.tl3p.backend.domain.ai.dto.AIDescriptionResponse;
import com.sparta.tl3p.backend.domain.ai.repository.AIDescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIDescriptionService {
    private final AIDescriptionRepository aiDescriptionRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public AIDescriptionResponse generateDescription(AIDescriptionRequest request) {
        String url = apiUrl + "?key=" + apiKey;
    }
}
