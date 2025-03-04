package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.dto.UpdateProjectDTO;
import com.morning.taskapimain.entity.project.*;
import com.morning.taskapimain.entity.user.User;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.BadRequestException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.*;
import com.morning.taskapimain.service.kafka.KafkaNotificationService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectResourceRepository projectResourceRepository;
    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectTagRepository projectTagRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final KafkaNotificationService kafkaNotificationService;
    private final DatabaseClient client;

    /**
     * ✅ Проверка, есть ли у пользователя разрешение на действие в проекте
     */
    private Mono<Void> validateRequesterHasPermission(Long projectId, String token, Permission permission) {
        String requesterUsername = jwtService.extractUsername(token);
        return userRepository.findByUsername(requesterUsername)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(user -> projectUserRepository.getUserRoleInProject(projectId, user.getId())
                        .flatMap(roleName -> projectRoleRepository.hasPermission(roleName, projectId, permission)
                                .filter(Boolean::booleanValue)
                                .switchIfEmpty(Mono.error(new AccessException("You don't have permission for this action!")))))
                .then();
    }


    /**
     * ✅ Создание проекта
     */
    public Mono<Project> createProject(Project project, String token) {
        String ownerUsername = jwtService.extractUsername(token);
        return userRepository.findByUsername(ownerUsername)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(owner -> {
                    project.setOwnerId(owner.getId());
                    project.setCreatedAt(LocalDateTime.now());
                    project.setUpdatedAt(LocalDateTime.now());
                    return projectRepository.save(project)
                            .flatMap(savedProject ->
                                    projectRoleRepository.createDefaultRoles(savedProject.getId())
                                            .then(projectUserRepository.assignUserToProject(savedProject.getId(), owner.getId(), "Owner"))
                                            .thenReturn(savedProject)
                            );
                });
    }

    /**
     * ✅ Получение проекта по ID
     */
    public Mono<Project> getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundException("Project not found")));
    }

    /**
     * ✅ Обновление проекта с учетом тегов, статусов и ресурсов
     */
    public Mono<Project> updateProject(Long projectId, UpdateProjectDTO updatedProject, String token) {
        System.out.println(updatedProject.toString());
        return validateRequesterHasPermission(projectId, token, Permission.CAN_EDIT_PROJECT)
                .then(projectRepository.findById(projectId)
                        .switchIfEmpty(Mono.error(new NotFoundException("Project not found")))
                        .flatMap(existingProject -> {

                            // Обновляем основную информацию проекта
                            existingProject.setTitle(updatedProject.getTitle());
                            existingProject.setDescription(updatedProject.getDescription());
                            existingProject.setColor(updatedProject.getColor());
                            existingProject.setUpdatedAt(LocalDateTime.now());

                            // ⚡ Обновляем теги, статусы и ресурсы
                            return projectRepository.save(existingProject)
                                    .then(updateProjectTags(projectId, updatedProject.getTags()))
                                    .then(updateProjectStatuses(projectId, updatedProject.getStatuses()))
                                    .then(updateProjectResources(projectId, updatedProject.getResources()))
                                    .thenReturn(existingProject);
                        }));
    }


    /**
     * ✅ Вспомогательный метод для updateProject, вызывается только из него, так что проверка на права не нужна
     */
    private Mono<Void> updateProjectTags(Long projectId, List<ProjectTag> updatedTags) {
        return projectTagRepository.deleteTagsByProjectId(projectId)
                .thenMany(Flux.fromIterable(updatedTags)
                        .flatMap(tag -> projectTagRepository.saveTag(projectId, tag.getName(), tag.getColor())))
                .then();
    }

    /**
     * ✅ Вспомогательный метод для updateProject, вызывается только из него, так что проверка на права не нужна
     */
    private Mono<Void> updateProjectStatuses(Long projectId, List<ProjectStatus> updatedStatuses) {
        if (updatedStatuses.isEmpty()) {
            return Mono.error(new BadRequestException("A project must have at least one status!"));
        }

        return projectStatusRepository.deleteStatusesByProjectId(projectId)
                .thenMany(Flux.fromIterable(updatedStatuses)
                        .flatMap(status -> projectStatusRepository.saveStatus(projectId, status.getName(), status.getColor())))
                .then();
    }

    /**
     * ✅ Вспомогательный метод для updateProject, вызывается только из него, так что проверка на права не нужна
     */
    private Mono<Void> updateProjectResources(Long projectId, List<ProjectResource> updatedResources) {
        return projectResourceRepository.deleteResourcesByProjectId(projectId)
                .thenMany(Flux.fromIterable(updatedResources)
                        .flatMap(resource -> projectResourceRepository.saveResource(projectId, resource.getUrl(), resource.getDescription())))
                .then();
    }

    /**
     * ✅ Получение всех проектов пользователя
     */
    public Flux<Project> getUserProjects(String username) {
        return userRepository.findByUsername(username)
                .flatMapMany(user -> projectRepository.findByOwnerId(user.getId()));
    }

    /**
     * ✅ Удаление проекта (только владелец)
     */
    public Mono<Void> deleteProject(Long projectId, String token) {
        String username = jwtService.extractUsername(token);
        return getUserId(username)
                .flatMap(userId -> projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new NotFoundException("Project not found")))
                .flatMap(project -> {
                    if (!project.getOwnerId().equals(userId)) {
                        return Mono.error(new AccessException("You are not the owner of this project!"));
                    }
                    return projectRepository.delete(project);
                }));
    }

    /**
     * ✅ Добавление пользователя в проект
     */
    public Mono<Void> addUserToProject(Long projectId, String username, String roleName, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_MANAGE_MEMBERS)
                .then(userRepository.findByUsername(username)
                        .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                        .flatMap(user -> projectRoleRepository.findRoleByProjectIdAndName(projectId, roleName)
                                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Role %s not found", roleName))))
                                .flatMap(role -> projectUserRepository.assignUserToProject(projectId, user.getId(), role.getName())))
                );
    }

    /**
     * ✅ Удаление пользователя из проекта
     */
    public Mono<Void> removeUserFromProject(Long projectId, String username, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_MANAGE_MEMBERS)
                .then(userRepository.findByUsername(username)
                        .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                        .flatMap(user -> projectUserRepository.removeUserFromProject(projectId, user.getId())));
    }

    /**
     * ✅ Смена роли пользователя в проекте
     */
    public Mono<Void> changeUserRole(Long projectId, String username, String newRole, String token) {
        return validateRequesterHasPermission(projectId, token, Permission.CAN_MANAGE_ROLES)
                .then(userRepository.findByUsername(username)
                        .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                        .flatMap(user -> projectRoleRepository.findRoleByProjectIdAndName(projectId, newRole)
                                .switchIfEmpty(Mono.error(new NotFoundException("Role does not exist")))
                                .flatMap(role -> projectUserRepository.updateUserRole(projectId, user.getId(), role.getName()))
                        )
                );
    }

    /**
     * ✅ Получение всех пользователей в проекте
     */
    public Flux<User> getAllUsersInProject(Long projectId) {
        return projectUserRepository.findUsersByProjectId(projectId)
                .switchIfEmpty(Mono.error(new NotFoundException("Project not found")));
    }

    /**
     * ✅ Проверка, является ли пользователь владельцем проекта
     */
    public Mono<Boolean> isProjectOwner(Long projectId, String token) {
        return getUserId(jwtService.extractUsername(token))
                .flatMap(userId -> projectRepository.findById(projectId)
                .map(project -> project.getOwnerId().equals(userId))
                .defaultIfEmpty(false));
    }

    /**
     * ✅ Вспомогательный метод для получения ID пользователя
     */
    private Mono<Long> getUserId(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }
}
