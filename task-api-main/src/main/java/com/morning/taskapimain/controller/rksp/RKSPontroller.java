package com.morning.taskapimain.controller.rksp;

import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.service.UserService;
import lombok.RequiredArgsConstructor;

import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RefreshScope
@RequestMapping("/api/v1/rksp")
public class RKSPontroller {

    @Value("${example.rksp}")
    private String surname;

    private final UserService userService;

    @GetMapping("")
    public Mono<ProfileDTO> getUserProfile(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token, @RequestParam("username") String username){
        return userService.findProfileByUsernameWithWebClient(username, token).map(profileDTO -> {
            profileDTO.setFirstName(surname);
            return profileDTO;
        });
    }
}
