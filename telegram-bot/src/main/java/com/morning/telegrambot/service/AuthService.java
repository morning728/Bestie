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
    private final UserTokenService userTokenService;

    public String authenticateUser(Long chatId, String username, String password) {
        AuthRequest request = new AuthRequest(chatId, username, password);
        AuthResponse response = authClient.authenticate(request);

        if (response != null && response.getToken() != null) {
            userTokenService.saveToken(chatId, response.getToken());
            return response.getToken();
        }
        return null;
    }

    public String getTokenFromSecurity(Long chatId) {
        try {
            return authClient.getTokenByChatId(chatId).getToken();
        } catch (Exception e) {
            return null; // Если токена нет, возвращаем null
        }
    }

}



