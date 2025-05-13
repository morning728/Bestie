package com.morning.telegrambot.handler;

import com.morning.telegrambot.exception.TelegramExceptionTranslator;
import com.morning.telegrambot.service.AuthService;
import com.morning.telegrambot.service.TokenCacheService;
import com.morning.telegrambot.service.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthService authService;
    private final UserStateService userStateService;
    private final TelegramExceptionTranslator exceptionTranslator;
    private final TokenCacheService tokenCacheService;

    public String handleLogin(Long chatId, String telegramUsername, String messageText) {
        try {
            String state = userStateService.getUserState(chatId);

            if (state == null) {
                userStateService.setUserState(chatId, "LOGIN_WAITING_USERNAME");
                return "Введите ваш логин:";
            }

            if (state.equals("LOGIN_WAITING_USERNAME")) {
                userStateService.setUserState(chatId, "LOGIN_WAITING_PASSWORD:" + messageText);
                return "Введите ваш пароль:";
            }

            if (state.startsWith("LOGIN_WAITING_PASSWORD")) {
                String username = state.split(":", 2)[1];
                String password = messageText;

                String token = authService.authenticateUser(chatId, telegramUsername, username, password);
                userStateService.clearUserState(chatId);

                return token != null ? "✅ Успешный вход!" : "❌ Ошибка входа!";
            }

            return "Ошибка авторизации.";
        } catch (Exception e) {
            return exceptionTranslator.toUserMessage(chatId, e);
        }
    }

    public String handleClearCommand(Long chatId) {
        userStateService.clearUserState(chatId); // очищаем состояние
        tokenCacheService.deleteToken(chatId);    // очищаем токен, если используешь токены
        return "♻️ Состояние и токен очищены.";
    }

    public String handleGetAccountInfo(Long chatId) {
        String state = userStateService.getUserState(chatId);
        String token = tokenCacheService.getToken(chatId) == null ? "Отсутствует" : tokenCacheService.getToken(chatId);
        return String.format("""
            ℹ Ваше состояние: <b>%s</b> 
            ℹ Ваш токен: <b>%s</b>
""", state, token);
    }

}



