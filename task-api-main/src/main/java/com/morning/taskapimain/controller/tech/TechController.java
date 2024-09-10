package com.morning.taskapimain.controller.tech;

import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.mapper.ProjectMapper;
import com.morning.taskapimain.mapper.UserMapper;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.UserService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tech")
@CrudExceptionHandler
@AccessExceptionHandler
@BadRequestExceptionHandler
public class TechController {

    private final ProjectService projectService;


    @GetMapping("/accept")
    public Flux<Object> addUserToProject(@RequestParam(name = "data") String acceptationToken){
        return projectService.addUserToProjectByAcceptationToken(acceptationToken);

    }
}
