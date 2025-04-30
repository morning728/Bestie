package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.dto.UserDTO;
import com.morning.taskapimain.entity.user.Contacts;
import com.morning.taskapimain.entity.user.User;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.ProjectRepository;
import com.morning.taskapimain.repository.task.TaskRepository;
import com.morning.taskapimain.repository.UserRepository;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final JwtService jwtService;


    @Value("${application.security.db}")
    private String securityDatabasePath;
    @Value("${application.security.db-username}")
    private String securityDatabaseUsername;
    @Value("${application.security.db-password}")
    private String securityDatabasePassword;


/*    // Лучше удалить позже
    private static final String SELECT_USER_CONTACTS_QUERY =     """
    select username, telegram_id, email from public.auth_user
    """;*/
    // Лучше удалить позже
/*    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(securityDatabasePath,
                securityDatabaseUsername, securityDatabasePassword);
    }*/
/*    // Лучше удалить позже
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
    }*/


    /**
     * ✅ Получение пользователя по username
     */
    public Mono<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    /**
     * ✅ Получение пользователя по username
     */
    public Mono<User> getUserByToken(String token) {
        return userRepository.findByUsername(jwtService.extractUsername(token))
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    /**
     * ✅ Получение пользователя по startWith
     */
    public Flux<UserDTO> getUsersByStartWith(String startWith) {
        return userRepository.findFirst10ByUsernameStartingWithIgnoreCase(startWith)
                .map(UserDTO::fromUser);
    }

    /**
     * ✅ Получение пользователя по ID
     */
    public Mono<User> getUserById(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    /**
     * ✅ Получение пользователя по ID
     */
    public Mono<String> getUsernameById(Long userId) {
        return userRepository.findUsernameById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }


    /**
     * ✅ Получение списка usernames по списку userIds
     */
    public Mono<List<String>> getUsernamesByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Mono.just(List.of());
        }

        return Flux.fromIterable(userIds)
                .flatMap(this::getUsernameById) // для каждого id запрашиваем username
                .collectList(); // собираем все в список
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
     * ✅ Добавление пользователя
     */
    public Mono<User> addUser(String token) {
        String requesterUsername = jwtService.extractUsername(token);
        return userExists(requesterUsername)
                .flatMap(exists -> {
                    return exists ?
                            Mono.error(new AccessException("User already exists")) :
                            userRepository.saveWithUsername(requesterUsername);
                });
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
    public Mono<Long> getUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    public Mono<Contacts> findContactsByUsernameWithWebClient(String usernameToGetProfile, String yourToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/notification/v1/contacts")
                        .queryParam("username", usernameToGetProfile)
                        .build())
                .header("Authorization",  yourToken)
                .retrieve()
                .bodyToMono(Contacts.class);
    }

/*    public Mono<Contacts> findContactsByUserIdWithWebClient(Long userId, String yourToken) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .flatMap(username -> {
                    return findContactsByUsernameWithWebClient(username, yourToken);
                });
    }*/


}
