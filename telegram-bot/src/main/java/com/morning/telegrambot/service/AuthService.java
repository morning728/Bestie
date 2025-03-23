package com.morning.telegrambot.service;

import com.morning.telegrambot.client.AuthClient;
import com.morning.telegrambot.dto.AuthRequest;
import com.morning.telegrambot.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;
    private final TokenCacheService tokenCacheService;

    public String authenticateUser(Long chatId, String username, String password) {
        AuthRequest request = new AuthRequest(chatId, username, password);
        AuthResponse response = authClient.authenticate(request);

        if (response != null && response.getToken() != null) {
            tokenCacheService.saveToken(chatId, response.getToken());
            return response.getToken();
        }
        return null;
    }

    public String getTokenForUser(Long chatId) {
        // 1. Пробуем из Redis
        String token = tokenCacheService.getToken(chatId);

        // 2. Если нет — пробуем у auth-сервиса
        if (token == null) {
            try {
                AuthResponse response = authClient.getTokenByChatId(chatId);
                if (response != null && response.getToken() != null) {
                    token = response.getToken();
                    tokenCacheService.saveToken(chatId, token);
                }
            } catch (Exception e) {
                // логирование можно добавить
                return null;
            }
        }

        return token;
    }

    public void logout(Long chatId) {
        tokenCacheService.deleteToken(chatId);
    }
}




