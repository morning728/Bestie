package com.morning.taskapimain.controller.user;

import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.mapper.ProjectMapper;
import com.morning.taskapimain.mapper.UserMapper;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.UserService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/profile")
public class ProfileController {
    private final UserService userService;
    private final ProjectService projectService;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;
    private final JwtService jwtService;

    @GetMapping("")
    public Mono<ProfileDTO> getUserProfile(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        return userService.findProfileByUsername(token);
    }
}
