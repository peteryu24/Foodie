package com.sparta.tl3p.backend.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeminiApiRequestDto {

    private List<Content> contents;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;

        public static Content from(String text) {
            return Content.builder()
                    .parts(List.of(Part.from(text)))
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;

        public static Part from(String text) {
            return Part.builder()
                    .text(text)
                    .build();
        }
    }

    public static GeminiApiRequestDto from(String text) {
        return GeminiApiRequestDto.builder()
                .contents(List.of(Content.from(text)))
                .build();
    }
}