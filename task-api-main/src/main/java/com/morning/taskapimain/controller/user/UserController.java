package com.morning.taskapimain.controller.user;

import com.morning.taskapimain.entity.dto.UserDTO;
import com.morning.taskapimain.mapper.ProjectMapper;
import com.morning.taskapimain.mapper.UserMapper;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.UserService;
import com.morning.taskapimain.service.security.JwtService;
import io.jsonwebtoken.Header;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final ProjectService projectService;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;
    private final JwtService jwtService;

    @GetMapping("")
    public Mono<UserDTO> getUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        String username = jwtService.extractUsername(token);
        return userService.getUserByUsername(username).map(userMapper::map);
    }

}
