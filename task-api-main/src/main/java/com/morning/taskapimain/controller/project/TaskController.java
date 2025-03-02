/*
package com.morning.taskapimain.controller.project;

import com.morning.taskapimain.entity.task.Task;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
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
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor
@CrudExceptionHandler
@AccessExceptionHandler
@BadRequestExceptionHandler
public class TaskController {
    private final JwtService jwtService;
    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping("/{projectId}/tasks")
    public Flux<TaskDTO> getAllProjectTasks(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token,
                                         @PathVariable(name = "projectId") Long projectId,
                                         @RequestParam(name = "field_id", required = false) Long fieldId){
        if(fieldId != null){
            return taskService.getTasksByProjectIdAndFieldId(projectId,fieldId, token);
        }
        return taskService.getTasksByProjectId(projectId, token);
    }

*/
/*    @GetMapping("/{projectId}/tasks")
    public Flux<Task> getAllProjectTasksWithParams(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token,
                                         @PathVariable(name = "projectId") Long projectId,
                                         @RequestParam(name = "field_id") Long fieldId){
        return taskService.getTasksByProjectIdAndFieldId(projectId,fieldId, token);
    }*//*

    @GetMapping("/{projectId}/tasks/{id}")
    public Mono<?> getTaskById(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token,
                               @PathVariable(name = "projectId") Long projectId,
                               @PathVariable(name = "id") Long taskId){
        return taskService.getTaskIfHaveAccess(taskId, projectId, token);
    }

    @PostMapping("/{projectId}/tasks")
    public Mono<Task> addTask(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                              @PathVariable(name = "projectId") Long projectId,
                              @RequestBody TaskDTO dto){
        return taskService.addTask(dto, token);
    }
    @PutMapping("/{projectId}/tasks/{id}")
    public Mono<Task> updateTask(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token,
                                 @RequestBody TaskDTO dto,
                                 @PathVariable(name = "projectId") Long projectId,
                                 @PathVariable(name = "id") Long taskId){
        return taskService.updateTask(dto, token);
    }

    @DeleteMapping("/{projectId}/tasks/{id}")
    public Mono<ResponseEntity<String>> deleteTask(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                   @PathVariable(name = "projectId") Long projectId,
                                                   @PathVariable(name = "id") Long taskId){

        return taskService.deleteTaskById(taskId, projectId, token)
                .thenReturn(new ResponseEntity<String>("Task was successfully deleted!", HttpStatus.OK));
    }
}
*/
