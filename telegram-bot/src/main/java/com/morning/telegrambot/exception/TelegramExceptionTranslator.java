package com.morning.telegrambot.exception;

import com.morning.telegrambot.service.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramExceptionTranslator {
    private final UserStateService userStateService;

    public String toUserMessage(Long chatId, Throwable e) {
        userStateService.clearUserState(chatId);
        if (e instanceof UnauthorizedException) {
            return "🔐 Ошибка авторизации: проверьте логин и пароль.";
        }

        if (e instanceof ForbiddenException) {
            return "🚫 Доступ запрещён. Убедитесь, что у вас есть права.";
        }

        if (e instanceof ResourceNotFoundException) {
            return "❓ Ресурс не найден. Возможно, он был удалён.";
        }

        if (e instanceof InternalServerErrorException) {
            return "💥 Внутренняя ошибка сервера. Повторите попытку позже.";
        }

        if (e instanceof FeignClientException) {
            return "⚠️ Ошибка при обращении к сервису: " + e.getMessage();
        }

        // fallback
        return "❗ Произошла непредвиденная ошибка: " + e.getMessage();
    }
}

