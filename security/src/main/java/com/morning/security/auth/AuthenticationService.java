package com.morning.security.auth;

import com.morning.security.config.security.JwtEmailService;
import com.morning.security.config.security.JwtService;
import com.morning.security.emailSender.KafkaMailProducer;
import com.morning.security.token.Token;
import com.morning.security.token.TokenRepository;
import com.morning.security.token.TokenType;
import com.morning.security.user.User;
import com.morning.security.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtEmailService jwtEmailService;
    private final KafkaMailProducer kafkaMailProducer;

    @Value("${application.task-api.db-url}")
    private String dbUrl;

    public String register(RegisterRequest request, HttpServletResponse response) throws SQLException {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .verified(false)
                .build();
        var savedUser = repository.save(user);

        if (request.getEmail() != null) {
            kafkaMailProducer.sendMailNotification(request.getUsername(), request.getEmail());
        }
        try {
            addUserToDatabase(request.getUsername()); // Добавляем в `app_user`
        } catch (Exception e){
            repository.deleteById(savedUser.getId());
            throw e;
        }

        var jwtToken = jwtService.generateToken(Map.of("role", "USER"), user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken, null);

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

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken, null);

        setRefreshTokenCookie(response, refreshToken);

        return jwtToken;
    }

    public String authenticateTelegram(String username, String password, Long chatId, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        var user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Если chatId еще не установлен — добавляем его в БД
        if (user.getChatId() == null) {
            user.setChatId(chatId);
            repository.save(user);
        } else if (!user.getChatId().equals(chatId)) {
            throw new RuntimeException("Этот аккаунт уже привязан к другому Telegram ID!");
        }

        var jwtToken = jwtService.generateToken(Map.of("role", user.getRole()), user);
        var refreshToken = jwtService.generateLongLivedToken(user); // 99 лет

        // 🔹 НЕ отзываем старые токены (чтобы оставить доступ к сайту)
        saveUserToken(user, jwtToken, chatId); // Просто добавляем токен с chatId

        setRefreshTokenCookie(response, refreshToken);

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
        revokeAllUserTokens(user);
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

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
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

    private void addUserToDatabase(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection(dbUrl, "postgres", "root")) {
            String insertQuery = "INSERT INTO \"app_user\" (username, created_at, updated_at, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, username);
                statement.setDate(2, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                statement.setDate(3, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
                statement.setString(4, "ACTIVE");
                int count = statement.executeUpdate();
                if (count != 1) {
                    throw new SQLException("User was not added to app_user");
                }
            }
        }
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
}
