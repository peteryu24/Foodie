package com.sparta.tl3p.backend.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    private static final Dotenv dotenv = Dotenv.load();

    @Value("${api.gemini.key}")
    private String geminiApiKey;

    @Value("${api.gemini.url}")
    private String geminiApiUrl;

    public String getGeminiApiKey() {
        return dotenv.get("GEMINI_API_KEY", geminiApiKey);
    }

    public String getGeminiApiUrl() {
        return dotenv.get("GEMINI_API_URL", geminiApiUrl);
    }
}
