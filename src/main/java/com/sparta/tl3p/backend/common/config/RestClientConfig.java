package com.sparta.tl3p.backend.common.config;

import com.sparta.tl3p.backend.common.exception.BusinessException;
import com.sparta.tl3p.backend.common.type.ErrorCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultHeaders(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .requestFactory(getClientHttpRequestFactory())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError , (req, res) -> {
                    throw new BusinessException(ErrorCode.REST_CLIENT_ERROR);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError , (req, res) -> {
                    throw new BusinessException(ErrorCode.API_SERVER_ERROR);
                })
                .build();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(10000);

        return factory;
    }
}