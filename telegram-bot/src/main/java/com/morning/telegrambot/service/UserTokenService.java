package com.morning.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserTokenService {

    private final StringRedisTemplate redisTemplate;
    private final AuthService authService;
    private static final String TOKEN_PREFIX = "user_token:";

    public void saveToken(Long chatId, String token) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + chatId, token, 24, TimeUnit.HOURS); // 24 часа
    }

    public String getToken(Long chatId) {
        String token = redisTemplate.opsForValue().get(TOKEN_PREFIX + chatId);

        if (token == null) {
            token = authService.getTokenFromSecurity(chatId); // Запрашиваем у security

            if (token != null) {
                saveToken(chatId, token); // Кэшируем на 24 часа
            }
        }

        return token;
    }


    public void deleteToken(Long chatId) {
        redisTemplate.delete(TOKEN_PREFIX + chatId);
    }
}


