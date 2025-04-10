package com.morning.security.telegramDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TelegramAuthRequest {
    private Long chatId;
    private String username;
    private String password;
}
