package com.sparta.tl3p.backend.domain.ai.dto;

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
}
