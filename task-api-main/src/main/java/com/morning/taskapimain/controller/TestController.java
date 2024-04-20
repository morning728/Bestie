package com.morning.taskapimain.controller;

import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.mapper.FieldMapper;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/test")
@RequiredArgsConstructor
@CrudExceptionHandler
@AccessExceptionHandler
public class TestController {
    private final JwtService jwtService;
    private final ProjectService projectService;


/*    @GetMapping("/{id}")
    public Mono<Boolean> test(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token, @PathVariable("id") Long id){
        return projectService.findProjectFieldsByProjectId(id, token);
    }*/
}
