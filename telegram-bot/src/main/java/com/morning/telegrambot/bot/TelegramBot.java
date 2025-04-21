package com.morning.telegrambot.bot;

import com.morning.telegrambot.config.BotConfig;
import com.morning.telegrambot.exception.TelegramExceptionTranslator;
import com.morning.telegrambot.handler.AuthHandler;
import com.morning.telegrambot.handler.ProjectHandler;
import com.morning.telegrambot.handler.TaskHandler;
import com.morning.telegrambot.service.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final AuthHandler authHandler;
    private final TaskHandler taskHandler;
    private final ProjectHandler projectHandler;
    private final UserStateService userStateService;
    private final BotConfig botConfig;
    private final TelegramExceptionTranslator exceptionTranslator;

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String responseText;

            try {
                // 1. Команда /cancel — всегда обрабатывается первой
                if (messageText.equalsIgnoreCase("/cancel")) {
                    userStateService.clearUserState(chatId);
                    responseText = "🚫 Текущее действие отменено. Вы можете начать заново.";
                }
                // 2. Получаем текущее состояние пользователя
                else {
                    String userState = userStateService.getUserState(chatId);

                    if (userState != null && userState.startsWith("LOGIN_")) {
                        responseText = authHandler.handleLogin(chatId, messageText);
                    } else if (messageText.startsWith("/login")) {
                        responseText = authHandler.handleLogin(chatId, messageText);
                    } else if (messageText.startsWith("/tasks")) {
                        responseText = taskHandler.handleTaskCommand(chatId, messageText);
                    } else if (messageText.startsWith("/projects")) {
                        responseText = projectHandler.handleProjectCommand(chatId, messageText);
                    } else if (messageText.startsWith("/help")) {
                        responseText = getHelpMessage();
                    } else if (messageText.startsWith("/clear")) {
                        responseText = authHandler.handleClearCommand(chatId);
                    } else if (messageText.startsWith("/debug")) {
                        responseText = authHandler.handleGetAccountInfo(chatId);
                    } else {
                        responseText = "Неизвестная команда. Используйте /help.";
                    }
                }
            } catch (Exception e) {
                // последний уровень перехвата ошибок
                responseText = exceptionTranslator.toUserMessage(chatId, e); // удобно!
                e.printStackTrace(); // оставить пока разрабатываешь
            }

            sendMessage(chatId, responseText);
        }
    }



    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.enableHtml(true); // 👈 Включаем HTML-форматирование
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendNotification(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.enableHtml(false); // 👈 Включаем HTML-форматирование
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getHelpMessage() {
        return """
            ℹ <b>Доступные команды:</b>

<b>👤 АВТОРИЗАЦИЯ</b>
/login   — Войти в аккаунт
/cancel  — Отменить текущее действие
/clear - очистить бд по аккаунту
/debug - посмотреть статус по аккаунту

<b>📋 ЗАДАЧИ</b>
/tasks         — Список ваших задач
/task_create   — Создать задачу (в разработке)

<b>📁 ПРОЕКТЫ</b>
/projects      — Список ваших проектов

<b>❓ ОБЩИЕ</b>
/help    — Показать это меню
""";
    }
}


