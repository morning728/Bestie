package com.morning.notification.entity.user;

import lombok.Data;

@Data
public class TelegramDataEvent {
    private String action;
    private String username;
    private String telegramId;
    private Long chatId;
}
