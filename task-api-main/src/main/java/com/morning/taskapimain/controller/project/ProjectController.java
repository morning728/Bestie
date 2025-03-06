package com.morning.taskapimain.controller.project;


import com.morning.taskapimain.entity.dto.UpdateProjectDTO;
import com.morning.taskapimain.entity.dto.UserToProjectDTO;
import com.morning.taskapimain.entity.dto.UserWithRoleDTO;
import com.morning.taskapimain.entity.project.Project;
import com.morning.taskapimain.entity.project.ProjectRole;
import com.morning.taskapimain.entity.user.User;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.repository.ProjectRoleRepository;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@CrudExceptionHandler
@AccessExceptionHandler
@BadRequestExceptionHandler
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRoleRepository projectRoleRepository;

    /**
     * ✅ Создание нового проекта
     */
    @PostMapping
    public Mono<ResponseEntity<Project>> createProject(@RequestBody Project project,
                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.createProject(project, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Получение информации о проекте по ID
     */
    @GetMapping("/{projectId}")
    public Mono<ResponseEntity<Project>> getProjectById(@PathVariable Long projectId) {
        return projectService.getProjectById(projectId)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Обновление проекта (название, описание, цвет, статусы, теги, ресурсы)
     */
    @PutMapping("/{projectId}")
    public Mono<ResponseEntity<Project>> updateProject(@PathVariable Long projectId,
                                                       @RequestBody UpdateProjectDTO updateProjectDTO,
                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.updateProject(projectId, updateProjectDTO, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Удаление проекта
     */
    @DeleteMapping("/{projectId}")
    public Mono<ResponseEntity<Void>> deleteProject(@PathVariable Long projectId,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.deleteProject(projectId, token)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    /**
     * ✅ Получение всех проектов пользователя
     */
    @GetMapping("/my")
    public Flux<Project> getUserProjects(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.getUserProjects(token);
    }

    /**
     * ✅ Добавление пользователя в проект с ролью
     */
    @PostMapping("/{projectId}/users")
    public Mono<ResponseEntity<Void>> addUserToProject(@PathVariable Long projectId,
                                                       @RequestBody UserToProjectDTO userToProjectDTO,
                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.addUserToProject(projectId, userToProjectDTO.getUsername(), userToProjectDTO.getRoleId(), token)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * ✅ Удаление пользователя из проекта
     */
    @DeleteMapping("/{projectId}/users/{username}")
    public Mono<ResponseEntity<Void>> removeUserFromProject(@PathVariable Long projectId,
                                                            @PathVariable String username,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.removeUserFromProject(projectId, username, token)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    /**
     * ✅ Смена роли пользователя в проекте
     */
    @PutMapping("/{projectId}/users")
    public Mono<ResponseEntity<Void>> changeUserRole(@PathVariable Long projectId,
                                                     @RequestBody UserToProjectDTO userToProjectDTO,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.changeUserRole(projectId, userToProjectDTO.getUsername(), userToProjectDTO.getRoleId(), token)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * ✅ Получение всех пользователей в проекте
     */
    @GetMapping("/{projectId}/users")
    public Flux<UserWithRoleDTO> getAllUsersInProject(@PathVariable Long projectId) {
        return projectService.getAllUsersInProject(projectId);
    }

    /**
     * ✅ Проверка, является ли пользователь владельцем проекта
     */
    @GetMapping("/{projectId}/is-owner")
    public Mono<ResponseEntity<Boolean>> isProjectOwner(@PathVariable Long projectId,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.isProjectOwner(projectId, token)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{projectId}/roles")
    public Mono<ResponseEntity<ProjectRole>> addRole(@PathVariable Long projectId,
                                                     @RequestBody ProjectRole projectRole,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.addRole(projectId, projectRole, token)
                .map(ResponseEntity::ok);
    }


    @PutMapping("/{projectId}/roles")
    public Mono<ResponseEntity<ProjectRole>> updateRole(@PathVariable Long projectId,
                                                        @RequestBody ProjectRole projectRole,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.updateRole(projectId, projectRole, token)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{projectId}/roles/{projectRoleId}")
    public Mono<ResponseEntity<Void>> deleteRole(@PathVariable Long projectId,
                                                 @PathVariable Long projectRoleId,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.deleteRole(projectId, projectRoleId, token)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/{projectId}/roles")
    public Flux<ProjectRole> getRolesByProjectId(@PathVariable Long projectId,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.getRolesByProjectId(projectId, token);
    }

    @GetMapping("/{projectId}/roles/my")
    public Mono<ProjectRole> getMyRoleByProjectId(@PathVariable Long projectId,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.getMyRoleByProjectId(projectId, token);
    }

}
