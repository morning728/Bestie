package com.morning.taskapimain.controller.project;

import com.morning.taskapimain.entity.Task;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.TaskService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/task")
@RequiredArgsConstructor
public class TaskController {
    private final JwtService jwtService;
    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping("/{id}")
    public Mono<Task> getTaskById(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token, @PathVariable String id){
        return taskService.getTaskByIdCheckingOwner(Long.valueOf(id), token);
    }
}
