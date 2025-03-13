package com.morning.telegrambot.bot;

import com.morning.telegrambot.config.BotConfig;
import com.morning.telegrambot.handler.AuthHandler;
import com.morning.telegrambot.handler.ProjectHandler;
import com.morning.telegrambot.handler.TaskHandler;
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

    @Override
    public String getBotUsername() {
        return "YOUR_BOT_USERNAME";
    }

    @Override
    public String getBotToken() {
        return "YOUR_BOT_TOKEN";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String responseText;

            if (messageText.startsWith("/login")) {
                responseText = authHandler.handleLogin(chatId, messageText);
            } else if (messageText.startsWith("/tasks")) {
                responseText = taskHandler.handleTaskCommand(chatId, messageText);
            } else if (messageText.startsWith("/projects")) {
                responseText = projectHandler.handleProjectCommand(chatId, messageText);
            } else {
                responseText = "Неизвестная команда. Используйте /help.";
            }

            sendMessage(chatId, responseText);
        }
    }


    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}


