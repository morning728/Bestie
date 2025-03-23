package com.morning.telegrambot.client;


import com.morning.telegrambot.dto.AuthRequest;
import com.morning.telegrambot.dto.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "authClient", url = "http://localhost:8765/security/v1/auth")
public interface AuthClient {

    @PostMapping("/authenticate")
    AuthResponse authenticate(@RequestBody AuthRequest request);

    @GetMapping("/get-telegram-token/{chatId}")
    AuthResponse getTokenByChatId(@PathVariable Long chatId);
}

