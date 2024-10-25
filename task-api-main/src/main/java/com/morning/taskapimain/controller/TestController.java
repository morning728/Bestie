package com.morning.taskapimain.controller;

import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
