package com.morning.telegrambot.handler;

import com.morning.telegrambot.service.AuthService;
import com.morning.telegrambot.service.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthService authService;
    private final UserStateService userStateService;

    public String handleLogin(Long chatId, String messageText) {
        if (userStateService.getUserState(chatId) == null) {
            userStateService.setUserState(chatId, "WAITING_USERNAME");
            return "Введите ваш логин:";
        }

        String state = userStateService.getUserState(chatId);
        if (state.equals("WAITING_USERNAME")) {
            userStateService.setUserState(chatId, "WAITING_PASSWORD:" + messageText);
            return "Введите ваш пароль:";
        } else if (state.startsWith("WAITING_PASSWORD")) {
            String username = state.split(":")[1];
            String password = messageText;

            String token = authService.authenticateUser(chatId, username, password);
            userStateService.clearUserState(chatId);

            return token != null ? "✅ Успешный вход!" : "❌ Ошибка входа!";
        }

        return "Ошибка обработки логина.";
    }

    public String handleAuthCommand(Long chatId) {
        userStateService.setUserState(chatId, "WAITING_USERNAME");
        return "Введите ваш логин:";
    }

}



