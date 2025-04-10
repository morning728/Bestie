package com.morning.notification.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "telegram-bot", url = "${application.telegram-bot.url-to-send}") // <-- URL задаём в application.yml
public interface TelegramClient {

    @PostMapping("/send") // роут, который реализован в telegram-bot
    void sendMessage(@RequestBody Map<String, String> request);
}

