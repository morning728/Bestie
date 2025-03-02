package com.morning.taskapimain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.taskapimain.entity.user.Contacts;
import com.morning.taskapimain.entity.user.User;
import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.ProjectRepository;
import com.morning.taskapimain.repository.TaskRepository;
import com.morning.taskapimain.repository.UserRepository;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final JwtService jwtService;

    @Value("${application.security.userInfoPath}")
    private String userInfoPath;
    @Value("${application.security.db}")
    private String securityDatabasePath;
    @Value("${application.security.db-username}")
    private String securityDatabaseUsername;
    @Value("${application.security.db-password}")
    private String securityDatabasePassword;


    private static final String SELECT_USER_CONTACTS_QUERY =     """
    select username, telegram_id, email from public.auth_user
    """;

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(securityDatabasePath,
                securityDatabaseUsername, securityDatabasePassword);
    }
    public Mono<Contacts> getUserContactsByUsername(String username) {

        return Mono.fromCallable(() -> {
                    Connection connection = getConnection();

                    String query = SELECT_USER_CONTACTS_QUERY + " WHERE username = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, username);

                    ResultSet resultSet = statement.executeQuery();

                    Contacts contacts = new Contacts();
                    if (resultSet.next()) {
                        contacts.setUsername(resultSet.getString("username"));
                        contacts.setEmail(resultSet.getString("email"));
                        contacts.setTelegramId(resultSet.getString("telegram_id"));
                    }

                    // Закрываем ресурсы
                    resultSet.close();
                    statement.close();
                    connection.close();

                    return contacts;
                })
                .subscribeOn(Schedulers.boundedElastic()); // Выполняем асинхронно
    }

    /**
     * ✅ Получение пользователя по username
     */
    public Mono<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    /**
     * ✅ Получение пользователя по ID
     */
    public Mono<User> getUserById(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    /**
     * ✅ Проверка существования пользователя по username
     */
    public Mono<Boolean> userExists(String username) {
        return userRepository.findByUsername(username)
                .hasElement();
    }

    /**
     * ✅ Получение всех пользователей
     */
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * ✅ Обновление информации о пользователе
     */
    public Mono<User> updateUser(String username, User updatedUser, String token) {
        String requesterUsername = jwtService.extractUsername(token);
        if (!requesterUsername.equals(username)) {
            return Mono.error(new AccessException("You can only update your own profile"));
        }

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(existingUser -> {
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                });
    }

    /**
     * ✅ Удаление пользователя
     */
    public Mono<Void> deleteUser(String username, String token) {
        String requesterUsername = jwtService.extractUsername(token);
        if (!requesterUsername.equals(username)) {
            return Mono.error(new AccessException("You can only delete your own profile"));
        }

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(userRepository::delete);
    }

    /**
     * ✅ Получение ID пользователя по username
     */
    private Long getUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .block();
    }






















    private HttpEntity<String> createHttpEntity(ProfileDTO dto, HttpHeaders headers) throws JsonProcessingException {
        HashMap<String, String> map = new HashMap<>();
        if(dto.getUsername() != null){
            map.put("username", dto.getUsername());
        }
        if(dto.getEmail() != null){
            map.put("email", dto.getEmail());
        }
        if(dto.getTelegramId() != null){
            map.put("telegramId", dto.getTelegramId());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jacksonData = objectMapper.writeValueAsString(map);
        return new HttpEntity<>((jacksonData), headers);
    }

    public Mono<ProfileDTO> findProfileByUsernameWithWebClient(String usernameToGetProfile, String yourToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/info")
                        .queryParam("username", usernameToGetProfile)
                        .build())
                .header("Authorization",  yourToken)
                .retrieve()
                .bodyToMono(ProfileDTO.class);
    }


}
