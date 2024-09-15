package com.morning.taskapimain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.entity.dto.UserDTO;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.mapper.UserMapper;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RestTemplate template;
    private final JwtService jwtService;
    private final DatabaseClient client;
    private final UserMapper userMapper;
    @Value("${application.security.userInfoPath}")
    private String userInfoPath;
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
                .defaultIfEmpty(User.defaultIfEmpty())
                .onErrorReturn(User.defaultIfEmpty())
                .flatMap(user -> {
                    if(!user.isEmpty()){
                        return Mono.just(user);
                    }
                    return Mono.error(new NotFoundException("User was not found!"));
                });
    }
    public Mono<UserDTO> getUserWithRoleByToken(String token, Long projectId){
        return userRepository.findByUsername(jwtService.extractUsername(token))
                .defaultIfEmpty(User.defaultIfEmpty())
                .onErrorReturn(User.defaultIfEmpty())
                .flatMap(user -> {
                    if(!user.isEmpty()){
                        return findRoleInProjectByToken(projectId, token).flatMap(s ->{
                            UserDTO dto = UserDTO.fromUser(user);
                            dto.setRole(s);
                            return Mono.just(dto);
                        });
                    }
                    return Mono.error(new NotFoundException("User was not found!"));
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
