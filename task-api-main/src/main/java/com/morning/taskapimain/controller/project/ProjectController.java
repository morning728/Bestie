package com.morning.taskapimain.controller.project;

import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.repository.ProjectRepository;
import com.morning.taskapimain.repository.ProjectRepositoryImpl;
import com.morning.taskapimain.repository.UserRepository;
import com.morning.taskapimain.service.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ProjectRepositoryImpl testRep;
    private final ProjectRepository projectRepository;
    @GetMapping("/test")
    public Mono<Project> getTest(/*@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token*/) throws InterruptedException {
//        String username = jwtService.extractUsername(token);
//        String role = jwtService.extractRole(token);
        projectRepository.findById(1L).subscribe(v -> System.out.println(v));
        Thread.sleep(20000);
        Mono<Project> map = projectRepository.findById(1L);
        return map;
    }

    @GetMapping("/test2")
    public Flux<Project> getProjectsById() throws InterruptedException {
        return testRep.findAll();
        //return null;
    }
}
