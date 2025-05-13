package com.morning.telegrambot.controller;

import com.morning.telegrambot.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/tg-bot/v1")
public class TelegramController {

    @Autowired
    private TelegramBot bot;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody Map<String, String> payload) {
        String chatId = payload.get("chatId");
        String message = payload.get("message");

        bot.sendNotification(Long.valueOf(chatId), message); // обращается к Telegram API
        return ResponseEntity.ok().build();
    }
}
