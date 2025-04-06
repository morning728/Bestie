package com.morning.notification.service;

import com.morning.notification.feign.TelegramClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final TelegramClient telegramClient;

    public void sendMessage(String chatId, String text) {
        telegramClient.sendMessage(Map.of(
                "chatId", chatId,
                "message", text
        ));
    }
}
