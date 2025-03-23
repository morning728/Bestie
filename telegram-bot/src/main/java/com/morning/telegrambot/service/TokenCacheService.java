package com.morning.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenCacheService {

    private final StringRedisTemplate redisTemplate;
    private static final String TOKEN_PREFIX = "user_token:";

    public void saveToken(Long chatId, String token) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + chatId, token, 24, TimeUnit.HOURS);
    }

    public String getToken(Long chatId) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + chatId);
    }

    public void deleteToken(Long chatId) {
        redisTemplate.delete(TOKEN_PREFIX + chatId);
    }
}

