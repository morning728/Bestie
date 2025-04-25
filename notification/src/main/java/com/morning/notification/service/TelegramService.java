package com.morning.notification.service;

import com.morning.notification.entity.user.NotificationPreferences;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final WebClient webClient;

    public void sendMessage(NotificationPreferences preferences, String text) {
        if (preferences.getChatId() != null && preferences.getTelegramNotification()) {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/send")
                            .build())
                    .bodyValue(Map.of(
                            "chatId", preferences.getChatId(),
                            "message", text
                    ))
                    .retrieve();
        }
/*        telegramClient.sendMessage(Map.of(
                "chatId", chatId,
                "message", text
        ));*/
    }
}
