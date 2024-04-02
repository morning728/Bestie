package com.morning.taskapimain.controller.project;

import com.morning.taskapimain.entity.Task;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.TaskService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final JwtService jwtService;
    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping("")
    public Flux<Task> getAllTasks(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token){
        return taskService.getAllTasksByToken(token);
    }
    @GetMapping("/{id}")
    public Mono<Task> getTaskById(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token, @PathVariable String id){
        return taskService.getTaskByIdCheckingOwner(Long.valueOf(id), token);
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> addTask(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token, @RequestBody TaskDTO dto){
        return taskService.addTask(dto, token)
                .thenReturn(new ResponseEntity<>("Task was successfully added!", HttpStatus.OK))
                .onErrorReturn(new ResponseEntity<>("Task was not added, invalid data!", HttpStatus.BAD_REQUEST));
    }
    @PutMapping("/{id}")
    public Mono<Task> updateTask(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token,
                                 @RequestBody TaskDTO dto,
                                 @PathVariable(value = "id") Long id){
        return taskService.updateTask(dto, token);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteTask(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                           @PathVariable(value = "id") Long id){

        return taskService.deleteTaskById(id, token)
                .thenReturn(new ResponseEntity<>("Task was successfully deleted!", HttpStatus.OK))
                .onErrorReturn(new ResponseEntity<>("Task was not found!", HttpStatus.NOT_FOUND));
    }
}
