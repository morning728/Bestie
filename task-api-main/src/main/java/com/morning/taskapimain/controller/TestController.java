package com.morning.taskapimain.controller;


import com.morning.taskapimain.entity.project.ProjectRole;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.repository.ProjectRoleRepository;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/test")
@RequiredArgsConstructor
@CrudExceptionHandler
@AccessExceptionHandler
public class TestController {
    private final JwtService jwtService;
    private final ProjectService projectService;
    private final ProjectRoleRepository projectRoleRepository;


    @GetMapping("/")
    public Mono<ProjectRole> test(){
        return projectRoleRepository.findRoleByProjectIdAndName(16L, "Owner");
    }
}
