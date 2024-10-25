package com.morning.taskapimain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.taskapimain.entity.Contacts;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.entity.dto.UserDTO;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.BadRequestException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.UserRepository;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RestTemplate template;
    private final JwtService jwtService;
    private final DatabaseClient client;
    @Value("${application.security.userInfoPath}")
    private String userInfoPath;
    @Value("${application.security.db}")
    private String securityDatabasePath;
    @Value("${application.security.db-username}")
    private String securityDatabaseUsername;
    @Value("${application.security.db-password}")
    private String securityDatabasePassword;


    private static final String SELECT_QUERY =     """
    select u.id, u.username, u.first_name,u.last_name, u.status, u.created_at, u.updated_at from users  as u
    """;

    private static final String SELECT_ROLE_BY_PROJECT_ID_QUERY =     """
    select user_project.role
     from project  as p
    join user_project on p.id=user_project.project_id
    join users on user_project.user_id=users.id
    """;
    private static final String SELECT_USER_ROLE_IN_PROJECT =     """
    select user_project.role from project as p
    join user_project on p.id=user_project.project_id
    join users on user_project.user_id=users.id
    """;

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

    public static Mono<String> getUserRoleInProject(Long projectId,
                                                        String token,
                                                        JwtService jwtService,
                                                        DatabaseClient client){
        String username = jwtService.extractUsername(token);
        String query = String.format(
                "%s WHERE users.username = '%s' AND p.id = %s",
                SELECT_ROLE_BY_PROJECT_ID_QUERY,
                username,
                projectId
        );
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(stringObjectMap -> Mono.just(stringObjectMap.get("role").toString()));
    }

    public static Mono<Boolean> isManagerOfProjectOrError(Long projectId,
                                                        String token,
                                                        JwtService jwtService,
                                                        DatabaseClient client){
        String username = jwtService.extractUsername(token);
        String query = String.format(
                "%s WHERE users.username = '%s' AND p.id = %s AND user_project.role = 'ADMIN'",
                SELECT_QUERY,
                username,
                projectId
        );
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(Project::fromMap)
                .defaultIfEmpty(Project.defaultIfEmpty())
                .flatMap(project -> project.isEmpty() ?
                        Mono.error(new AccessException("You are not admin of project!")) :
                        Mono.just(true));
    }

    public Mono<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> findUserByUsername(String username) {
        String query = String.format("%s WHERE u.username = '%s'", SELECT_QUERY, username);
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(User::monoFromMap);
    }

    public Flux<User> findUsersByUsernameContains(String substring){
        return userRepository.findUserByUsernameContainsIgnoreCase(substring)
                .sort(Comparator.comparingInt(o -> o.getUsername().length()));
    }

    public Mono<Boolean> isManagerOfProject(Long projectId, String token){
        String username = jwtService.extractUsername(token);
        String query = String.format(
                "%s WHERE users.username = '%s' AND p.id = %s",
                SELECT_USER_ROLE_IN_PROJECT,
                username,
                projectId
        );
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(stringObjectMap -> Mono.just(stringObjectMap.get("role").toString()))
                .defaultIfEmpty("null")
                .flatMap(role -> role == "null" ?
                        Mono.error(new NotFoundException("Such user was not found in this project at all!")) :
                        Mono.just(role.equals("MANAGER")));
    }

    public Mono<Boolean> isAdminOfProject(Long projectId, String token){
        String username = jwtService.extractUsername(token);
        String query = String.format(
                "%s WHERE users.username = '%s' AND p.id = %s",
                SELECT_USER_ROLE_IN_PROJECT,
                username,
                projectId
        );
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(stringObjectMap -> Mono.just(stringObjectMap.get("role").toString()))
                .defaultIfEmpty("null")
                .flatMap(role -> role == "null" ?
                        Mono.error(new NotFoundException("Such user was not found in this project at all!")) :
                        Mono.just(role.equals("ADMIN")));
    }

    public Mono<String> findRoleInProjectByToken(Long projectId, String token){
        String username = jwtService.extractUsername(token);
        String query = String.format(
                "%s WHERE users.username = '%s' AND p.id = %s",
                SELECT_USER_ROLE_IN_PROJECT,
                username,
                projectId
        );
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(stringObjectMap -> Mono.just(stringObjectMap.get("role").toString()))
                .defaultIfEmpty("null")
                .flatMap(role -> role == "null" ?
                        Mono.error(new NotFoundException("Such user was not found in this project at all!")) :
                        Mono.just(role));
    }

    public Mono<User> getUserByToken(String token){
        return userRepository.findByUsername(jwtService.extractUsername(token))
                .switchIfEmpty(Mono.error(new NotFoundException("User was not found!")))
                .onErrorMap(e -> {
                    // Обработка и перехват всех ошибок, кроме NotFoundException
                    if (!(e instanceof NotFoundException)) {
                        return new BadRequestException("An unexpected error occurred" + e.toString());
                    }
                    return e; // Пробрасываем NotFoundException
                });
    }
    public Mono<UserDTO> getUserWithRoleByToken(String token, Long projectId) {
        String username = jwtService.extractUsername(token);

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException("User was not found!")))  // Ошибка, если пользователь не найден
                .zipWith(findRoleInProjectByToken(projectId, token))  // Получение пользователя и роли
                .map(tuple -> {
                    User user = tuple.getT1();
                    String role = tuple.getT2();
                    UserDTO dto = UserDTO.fromUser(user);
                    dto.setRole(role);
                    return dto;
                })
                .onErrorMap(e -> {
                    // Обработка и перехват всех ошибок, кроме NotFoundException
                    if (!(e instanceof NotFoundException)) {
                        return new BadRequestException("An unexpected error occurred" + e.toString());
                    }
                    return e; // Пробрасываем NotFoundException
                });
    }


    public Mono<ProfileDTO> findProfileByToken(String token){
        String username = jwtService.extractUsername(token);
        ResponseEntity<String> profileInfo;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add(
                    HttpHeaders.AUTHORIZATION,
                    token
            );
            //log.info(userInfoPath,headers.toString());

            HttpEntity<String> entity = new HttpEntity<>("body", headers);

            profileInfo = template.exchange(userInfoPath, HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            return Mono.error(new HttpClientErrorException(HttpStatus.FORBIDDEN));
//                    throw new RuntimeException("Access Denied!");
        }
        return findUserByUsername(username).flatMap(user1 -> Mono.just(new ProfileDTO(user1.getId(),
                user1.getUsername(),
                user1.getFirstName(),
                user1.getLastName(),
                user1.getCreatedAt())
                .getProfileInfoFromSecurityResponse(profileInfo.getBody())));
    }
    public Mono<ProfileDTO> findProfileByUsername(String usernameToGetProfile, String yourToken){
        ResponseEntity<String> profileInfo;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add(
                    HttpHeaders.AUTHORIZATION,
                    yourToken
            );
            //log.info(userInfoPath,headers.toString());

            HttpEntity<String> entity = new HttpEntity<>("body", headers);

            profileInfo = template.exchange(userInfoPath.concat("?username=").concat(usernameToGetProfile), HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            return Mono.error(new HttpClientErrorException(HttpStatus.FORBIDDEN));
//                    throw new RuntimeException("Access Denied!");
        }
        return findUserByUsername(usernameToGetProfile).flatMap(user1 -> Mono.just(new ProfileDTO(user1.getId(),
                user1.getUsername(),
                user1.getFirstName(),
                user1.getLastName(),
                user1.getCreatedAt())
                .getProfileInfoFromSecurityResponse(profileInfo.getBody())));
    }


    public Mono<User> updateProfileByToken(String token, ProfileDTO dto){
        String username = jwtService.extractUsername(token);
        ResponseEntity<String> profileInfo;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add(
                    HttpHeaders.AUTHORIZATION,
                    token
            );
            //HttpEntity<String> entity = createHttpEntity(dto, headers);
            HttpEntity<ProfileDTO> entity = new HttpEntity<>(dto, headers);

            profileInfo = template.exchange(userInfoPath, HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            return Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
//                    throw new RuntimeException("Access Denied!");
        }
        return findUserByUsername(username).defaultIfEmpty(User.defaultIfEmpty())
                .flatMap(user -> {
                    if(!user.getStatus().equals("EMPTY")){
                        user.updateByProfileDTO(dto);
                        return userRepository.save(user);
                    }
                    return Mono.error(new RuntimeException("User was not updated!"));
                });
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


}
