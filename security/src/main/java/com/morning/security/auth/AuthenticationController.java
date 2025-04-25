package com.morning.security.auth;


import com.morning.security.telegramDTO.TelegramAuthRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

@RestController
@RequestMapping("/security/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authService;;



  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request, HttpServletResponse response) throws SQLException {
    String accessToken = authService.register(request, response);
    return ResponseEntity.ok(Map.of("access_token", accessToken));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<Map<String, String>> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
    String accessToken = authService.authenticate(request, response);
    return ResponseEntity.ok(Map.of("access_token", accessToken));
  }

  @PostMapping("/authenticate/telegram")
  public ResponseEntity<Map<String, String>> authenticateTelegram(
          @RequestBody TelegramAuthRequest authRequest,
          HttpServletResponse response) {

    String accessToken = authService.authenticateTelegram(authRequest.getUsername(), authRequest.getPassword(), authRequest.getChatId(), authRequest.getTelegramId());
    return ResponseEntity.ok(Map.of("access_token", accessToken));
  }

  @PostMapping("/refresh")
  public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String accessToken = authService.refreshToken(request, response);
    return ResponseEntity.ok(Map.of("access_token", accessToken));
  }

  @GetMapping("/get-telegram-token/{chatId}")
  public ResponseEntity<String> getTelegramToken(@PathVariable Long chatId) {
    return !authService.getTelegramToken(chatId).equals("Token not found") ?
            ResponseEntity.ok(authService.getTelegramToken(chatId)) :
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен не найден");
  }



}
