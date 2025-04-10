package com.morning.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserStateService {

    private final StringRedisTemplate redisTemplate;
    private static final String STATE_PREFIX = "user_state:";

    public void setUserState(Long chatId, String state) {
        redisTemplate.opsForValue().set(STATE_PREFIX + chatId, state, 30, TimeUnit.MINUTES);
    }

    public String getUserState(Long chatId) {
        return redisTemplate.opsForValue().get(STATE_PREFIX + chatId);
    }

    public void clearUserState(Long chatId) {
        redisTemplate.delete(STATE_PREFIX + chatId);
    }
}

