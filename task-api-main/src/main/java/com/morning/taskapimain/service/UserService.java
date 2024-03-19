package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
    @Value("${application.security.userInfoPath}")
    private String userInfoPath;

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<ProfileDTO> getProfileByUsername(String username, String token){
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
        //Map<String, String> body = profileInfo.getBody().;
        Mono<User> user = userRepository.findByUsername(username);
        return Mono.just(ProfileDTO.builder()
                        .username(username)
                .build());
    }
}
