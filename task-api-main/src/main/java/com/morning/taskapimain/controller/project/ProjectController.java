package com.morning.taskapimain.controller.project;

import com.morning.taskapimain.entity.Project;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.entity.dto.UserProjectsRequest;
import com.morning.taskapimain.mapper.ProjectMapper;
import com.morning.taskapimain.repository.UserRepository;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final JwtService jwtService;
    private final ProjectService projectService;
    private final ProjectMapper mapper;
//    @GetMapping("/test")
//    public Mono<Project> getTest(/*@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token*/) throws InterruptedException {
//        String username = jwtService.extractUsername(token);
//        String role = jwtService.extractRole(token);
//        projectService.findById(1L).subscribe(v -> System.out.println(v));
//        Thread.sleep(20000);
//        Mono<Project> map = projectService.findById(1L);
//        return map;
//    }

    @GetMapping("/test2")
    public Flux<ProjectDTO> getProjectsByUserId(@RequestBody UserProjectsRequest request) {
        Long id = request.getUserId();
        return projectService.findAllByUserId(id).map(mapper::map);
    }
}
