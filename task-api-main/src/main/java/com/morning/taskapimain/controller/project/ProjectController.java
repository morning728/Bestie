package com.morning.taskapimain.controller.project;


import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.entity.dto.UpdateProjectDTO;
import com.morning.taskapimain.entity.dto.UserToProjectDTO;
import com.morning.taskapimain.entity.dto.UserWithRoleDTO;
import com.morning.taskapimain.entity.project.*;
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

import java.util.List;

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
     * ✅ Получение информации о проекте по ID
     */
    @GetMapping("/{projectId}/full-info")
    public Mono<ResponseEntity<ProjectDTO>> getFullProjectInfoById(@PathVariable Long projectId) {
        return projectService.getFullProjectInfoById(projectId)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Обновление проекта (название, описание, цвет)
     */
    @PutMapping("/{projectId}")
    public Mono<ResponseEntity<Project>> updateProject(@PathVariable Long projectId,
                                                       @RequestBody UpdateProjectDTO updateProjectDTO,
                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.updateProject(projectId, updateProjectDTO, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Обновление тегов проекта
     */
    @PutMapping("/{projectId}/tags")
    public Mono<ResponseEntity<ProjectTag>> updateProjectTag(@PathVariable Long projectId,
                                                             @RequestBody ProjectTag updatedTag,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.updateProjectTag(projectId, updatedTag, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Обновление тегов проекта
     */
    @PostMapping("/{projectId}/tags")
    public Mono<ResponseEntity<ProjectTag>> addProjectTag(@PathVariable Long projectId,
                                                          @RequestBody ProjectTag newTag,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.addProjectTag(projectId, newTag, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Обновление статусов проекта
     */
    @PutMapping("/{projectId}/statuses")
    public Mono<ResponseEntity<ProjectStatus>> updateProjectStatuses(@PathVariable Long projectId,
                                                                     @RequestBody ProjectStatus updatedStatus,
                                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.updateProjectStatus(projectId, updatedStatus, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Добавление статусов проекта
     */
    @PostMapping("/{projectId}/statuses")
    public Mono<ResponseEntity<ProjectStatus>> addProjectStatuses(@PathVariable Long projectId,
                                                                  @RequestBody ProjectStatus newStatus,
                                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.addProjectStatus(projectId, newStatus, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Обновление ресурсов проекта
     */
    @PutMapping("/{projectId}/resources")
    public Mono<ResponseEntity<ProjectResource>> updateProjectResource(@PathVariable Long projectId,
                                                                       @RequestBody ProjectResource updatedResource,
                                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.updateProjectResources(projectId, updatedResource, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Добавление ресурс проекта
     */
    @PostMapping("/{projectId}/resources")
    public Mono<ResponseEntity<ProjectResource>> addProjectResource(@PathVariable Long projectId,
                                                                    @RequestBody ProjectResource newResource,
                                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.addProjectResource(projectId, newResource, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Получение тегов проекта
     */
    @GetMapping("/{projectId}/tags")
    public Flux<ProjectTag> updateProjectTags(@PathVariable Long projectId,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.getProjectTags(projectId);
    }

    /**
     * ✅ Получение статусов проекта
     */
    @GetMapping("/{projectId}/statuses")
    public Flux<ProjectStatus> updateProjectStatuses(@PathVariable Long projectId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.getProjectStatuses(projectId);
    }

    /**
     * ✅ Получение ресурсов проекта
     */
    @GetMapping("/{projectId}/resources")
    public Flux<ProjectResource> updateProjectResources(@PathVariable Long projectId,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.getProjectResources(projectId);
    }

    /**
     * ✅ Удаление тегов проекта
     */
    @DeleteMapping("/{projectId}/tags/{tagId}")
    public Mono<ResponseEntity<Void>> deleteProjectTag(@PathVariable Long projectId,
                                                       @PathVariable Long tagId,
                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.deleteProjectTag(projectId, tagId, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Удаление status проекта
     */
    @DeleteMapping("/{projectId}/statuses/{statusId}")
    public Mono<ResponseEntity<Void>> deleteProjectStatus(@PathVariable Long projectId,
                                                          @PathVariable Long statusId,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.deleteProjectStatus(projectId, statusId, token)
                .map(ResponseEntity::ok);
    }

    /**
     * ✅ Удаление resource проекта
     */
    @DeleteMapping("/{projectId}/resources/{resourceId}")
    public Mono<ResponseEntity<Void>> deleteProjectResource(@PathVariable Long projectId,
                                                            @PathVariable Long resourceId,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.deleteProjectResource(projectId, resourceId, token)
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
        return projectService.getRolesByProjectId(projectId);
    }

    @GetMapping("/{projectId}/roles/my")
    public Mono<ProjectRole> getMyRoleByProjectId(@PathVariable Long projectId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.getMyRoleByProjectId(projectId, token);
    }

}
