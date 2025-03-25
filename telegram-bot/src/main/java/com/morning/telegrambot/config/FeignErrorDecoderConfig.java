package com.morning.telegrambot.config;

import com.morning.telegrambot.exception.*;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignErrorDecoderConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String errorMessage = response.request().url() + " вернул статус " + response.status();

            return switch (response.status()) {
                case 401 -> new UnauthorizedException("Неавторизованный доступ: " + errorMessage);
                case 403 -> new ForbiddenException("Доступ запрещен: " + errorMessage);
                case 404 -> new ResourceNotFoundException("Ресурс не найден: " + errorMessage);
                case 500, 502, 503 -> new InternalServerErrorException("Ошибка сервера: " + errorMessage);
                default -> new FeignClientException("Feign ошибка: " + errorMessage);
            };
        };
    }
}

