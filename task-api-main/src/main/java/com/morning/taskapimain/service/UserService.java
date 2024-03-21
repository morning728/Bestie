package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.entity.dto.ProfileDTO;
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
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

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
    private static final String SELECT_QUERY =     """
    select u.id, u.username, u.first_name,u.last_name, u.status, u.created_at, u.updated_at from users  as u
    """;

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> getUserByUsername(String username) {
        String query = String.format("%s WHERE u.username = '%s'", SELECT_QUERY, username);
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(User::fromMap);
    }

    public Mono<ProfileDTO> getProfileByUsername(String token){
        String username = jwtService.extractUsername(token);
        ResponseEntity<String> profileInfo;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add(
                    HttpHeaders.AUTHORIZATION,
                    token
            );

            HttpEntity<String> entity = new HttpEntity<>("body", headers);

            profileInfo = template.exchange(userInfoPath, HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            return Mono.error(new HttpClientErrorException(HttpStatus.FORBIDDEN));
//                    throw new RuntimeException("Access Denied!");
        }
        return getUserByUsername(username).flatMap(user1 -> Mono.just(new ProfileDTO(user1.getId(),
                user1.getUsername(),
                user1.getFirstName(),
                user1.getLastName(),
                user1.getCreatedAt())
                .getProfileInfoFromSecurityResponse(profileInfo.getBody())));
    }
}
