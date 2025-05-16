package com.morning.security.auth;

import com.morning.security.config.security.JwtEmailService;
import com.morning.security.config.security.JwtService;
import com.morning.security.emailSender.KafkaService;
import com.morning.security.token.Token;
import com.morning.security.token.TokenRepository;
import com.morning.security.token.TokenType;
import com.morning.security.user.User;
import com.morning.security.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtEmailService jwtEmailService;
    private final KafkaService kafkaService;
    private final WebClient webClient;


    public String register(RegisterRequest request, HttpServletResponse response) throws SQLException {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .verified(false)
                .build();
        if(repository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Username is already in use");
        }
        var savedUser = repository.save(user);

        var jwtToken = jwtService.generateToken(Map.of("role", "USER"), user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken, null);
        try {
            kafkaService.sendUsernameToNotificationService(request.getUsername());
            sendUsernameToMainApiService(jwtToken); // проверяем ответ
        } catch (Exception e) {
            log.error(e.getMessage());
            tokenRepository.deleteAllByAuthUserId(savedUser.getId());
            repository.deleteById(savedUser.getId());
            throw e;
        }

        setRefreshTokenCookie(response, refreshToken);

        return jwtToken;
    }

    public String authenticate(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(Map.of("role", user.getRole()), user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllNonTelegramUserTokens(user);
        saveUserToken(user, jwtToken, null);

        setRefreshTokenCookie(response, refreshToken);

        return jwtToken;
    }

    public String authenticateTelegram(String username, String password, Long chatId, String telegramId) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        var user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Если chatId еще не установлен — добавляем его в БД
        if (user.getChatId() == null) {
            user.setChatId(chatId);
            repository.save(user);
            kafkaService.sendTelegramDataChange(username, telegramId, chatId);
        } else if (!user.getChatId().equals(chatId)) {
            throw new RuntimeException("Этот аккаунт уже привязан к другому Telegram ID!");
        }

        var jwtToken = jwtService.generateLongLivedToken(user); // 99 лет

        // 🔹 НЕ отзываем старые токены (чтобы оставить доступ к сайту)
        revokeAllTelegramUserTokens(user, chatId);
        saveUserToken(user, jwtToken, chatId); // Просто добавляем токен с chatId

        return jwtToken;
    }



    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Refresh token отсутствует");
        }

        final String username = jwtService.extractUsername(refreshToken);
        if (username == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Некорректный токен");
        }

        var user = repository.findByUsername(username).orElseThrow();
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Refresh token недействителен");
        }

        var newAccessToken = jwtService.generateToken(Map.of("role", user.getRole()), user);
        revokeAllNonTelegramUserTokens(user);
        saveUserToken(user, newAccessToken, null);

        return newAccessToken;
    }

    private void saveUserToken(User user, String jwtToken, Long chatId) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .chatId(chatId) // Привязываем chatId, если это Telegram
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllNonTelegramUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidNonTelegramTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void revokeAllTelegramUserTokens(User user, Long chatId) {
        var validUserTokens = tokenRepository.findAllValidTelegramTokenByUser(user.getId(), chatId);
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 дней
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void sendUsernameToMainApiService(String token) {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/register")
                        .build())
                .header("Authorization", "Bearer ".concat(token))
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(), // Проверка, что НЕ 2xx
                        clientResponse -> {
                            // Бросаем ошибку если код не 2xx
                            return clientResponse.bodyToMono(String.class)
                                    .defaultIfEmpty("Unknown error")
                                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                                            "Failed to send username to main-api-service: " + errorBody
                                    )));
                        }
                )
                .toBodilessEntity() // Нам не нужен сам ответ, только статус
                .block(); // Блокируем чтобы отправка точно завершилась
    }

    public String getTelegramToken(Long chatId) {
        return tokenRepository.findByChatId(chatId).get().getToken() != null ?
                tokenRepository.findByChatId(chatId).get().getToken() :
                "Token not found";
    }

    public void verifyEmail(String token, String emailToken) {
        token = token.substring(7);
        User user = repository.findByUsername(jwtService.extractUsername(token)).orElseThrow();

        if (jwtEmailService.isTokenValid(emailToken, jwtService.extractUsername(token)) &&
                jwtEmailService.extractEmail(emailToken).equals(user.getEmail())) {
            user.setVerified(true);
            repository.save(user);
        } else {
            System.out.println("Not uraaa");
        }

    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        final String token = extractAccessToken(request);
        if (token == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Access token отсутствует");
        }

        final String username = jwtService.extractUsername(token);
        if (username == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Некорректный access token");
        }

        var user = repository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Пользователь не найден"));

        // Отзываем ВСЕ обычные токены пользователя
        revokeAllNonTelegramUserTokens(user);

        // Удаляем refresh_token куку
        deleteRefreshTokenCookie(response);
    }

    private String extractAccessToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // если у тебя HTTPS, поставь true
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // обнуляем куку
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }


}
