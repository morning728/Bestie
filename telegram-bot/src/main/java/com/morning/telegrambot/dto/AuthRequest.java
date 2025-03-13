package com.morning.telegrambot.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthRequest {
    private Long chatId;
    private String username;
    private String password;
}

