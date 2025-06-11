package com.morning.taskapimain.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${application.baseUrlPath}")
    private String baseUrl;

    @Bean(name = "webClient")
    @Qualifier("webClient")
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                .build();
    }

    // WebClient для GigaChat с прямыми HTTPS-запросами (например, к Python-сервису)
    @Bean
    @Qualifier("webClientGigaChat")
    public WebClient webClientGigaChat() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
