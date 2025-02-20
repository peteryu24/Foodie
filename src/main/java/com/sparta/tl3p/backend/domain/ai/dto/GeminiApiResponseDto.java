package com.sparta.tl3p.backend.domain.ai.dto;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GeminiApiResponseDto {
    private List<Candidate> candidates;

    @Getter
    @NoArgsConstructor
    public static class Candidate {
        private Content content;
    }

    @Getter
    @NoArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @NoArgsConstructor
    public static class Part {
        private String text;
    }

    public String extractText() {
        try {
            return candidates.get(0)
                    .getContent()
                    .getParts()
                    .get(0)
                    .getText();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.API_RESPONSE_PARSE_ERROR);
        }
    }
}