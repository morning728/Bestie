package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.Task;
import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.BadRequestException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.TaskRepository;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final DatabaseClient client;
    private final ProjectService projectService;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final JwtService jwtService;
    private static final String SELECT_QUERY =     """
    select t.id, t.name, t.description, t.status, t.created_at, t.updated_at, t.project_id, t.field_id from task  as t
    join project on t.project_id=project.id
    join field on t.field_id=field.id
    """;
    private static final String ADD_TASK_TO_USER_QUERY =     """
    insert into user_task (user_id, task_id) values (%s, %s)
    """;
    private static final String IS_USER_RESPONSIBLE_QUERY =     """
    select user_id, task_id from user_task where user_id = %s and task_id = %s
    """;
    private static final String FULL_SELECT_QUERY =     """
    select t.id, t.name, t.description, t.status, t.created_at, t.updated_at, t.project_id, t.field_id from task  as t
    join project on t.project_id=project.id
    join field on t.field_id=field.id
    join user_project on project.id=user_project.project_id
    join users on user_project.user_id=users.id
    """;
    private static final String CHECK_TASK_VISIBILITY =     """
    select project.visibility from task as t
    join project on t.project_id=project.id
    join field on t.field_id=field.id
    """;

    public Mono<Task> getTaskById(Long id) {
        String query = String.format("%s WHERE t.id = %s", SELECT_QUERY, id);
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(Task::fromMap)
                .defaultIfEmpty(Task.defaultIfEmpty())
                .flatMap(Task::returnExceptionIfEmpty);
    }
    public Mono<Boolean> isUserResponsibleForTaskOrError(Long userId, Long taskId) {
        String query = String.format(IS_USER_RESPONSIBLE_QUERY, userId, taskId);
        return client.sql(query)
                .fetch()
                .first()
                .defaultIfEmpty(null)
                .flatMap(stringObjectMap -> {
                    return stringObjectMap != null ?  Mono.just(true) : Mono.error(new AccessException("You are not responsible for this task"));
                });
    }

    public Flux<Task> getAllTasksByToken(String token){
        String username = jwtService.extractUsername(token);
        String query = String.format("%s WHERE users.username = '%s'", FULL_SELECT_QUERY,username);
        return client.sql(query)
                .fetch()
                .all()
                .flatMap(Task::fromMap)
                .defaultIfEmpty(Task.defaultIfEmpty())
                .flatMap(task -> task.isEmpty() ? Mono.error(new NotFoundException("No one task was found!")) : Mono.just(task));
    }
    /*Метод получения таски: если существует таска, включенная в проект, создателям которого является пользователь,
    * от которого поступил запрос, то она возвращается*/
    public Mono<Task> findByIdAndUsername(Long id, String username) {
        String query = String.format("%s WHERE t.id = %s AND users.username = '%s'", FULL_SELECT_QUERY, id, username);
        return client.sql(query)
                .fetch()
                .first()
                .flatMap(Task::fromMap)
                .defaultIfEmpty(Task.defaultIfEmpty())
                .flatMap(task -> task.isEmpty() ? Mono.error(new NotFoundException("Task was found!")) : Mono.just(task));
    }

    /*Метод получения таски: если таска открыта для всех, то выдается без проверок
    * Если открыта только создателю, то выдается только в случае, если запрос от создателя*/
    public Mono<Task> getTaskIfHaveAccess(Long taskId, Long projectId, String token) {
/*        String username = token != null ?
                jwtService.extractUsername(token) :
                "";
        return isOpenTaskById(id).flatMap(aBoolean -> {
            if(aBoolean){
                return getTaskById(id).defaultIfEmpty(Task.defaultIfEmpty()).flatMap(Task::returnExceptionIfEmpty);
            }
            String query = String.format(
                    "%s WHERE t.id = %s AND users.username = '%s'",
                    FULL_SELECT_QUERY, id, username);
            return client.sql(query)
                    .fetch()
                    .first()
                    .flatMap(Task::fromMap)
                    .defaultIfEmpty(Task.defaultIfEmpty())
                    .flatMap(Task::returnExceptionIfEmpty);
        });*/
        return projectService.hasAccessToProjectOrError(projectId, token)
                .then(taskRepository.findById(taskId));
    }

    public Flux<Task> getTasksByProjectId(Long projectId, String token){
        return projectService.hasAccessToProjectOrError(projectId, token)
                .thenMany(taskRepository.findTasksByProjectIdOrderById(projectId));
    }
    public Flux<Task> getTasksByProjectIdAndFieldId(Long projectId,Long fieldId, String token){
        return projectService.hasAccessToProjectOrError(projectId, token)
                .thenMany(taskRepository.findTasksByProjectIdAndFieldIdOrderById(projectId, fieldId));
    }
    public Mono<Task> addTask(TaskDTO dto, String token){
        Mono<Long> userId = userService.findUserByUsername(jwtService.extractUsername(token)).flatMap(user -> Mono.just(user.getId()));
        Mono<Boolean> isParticipant = projectService.isParticipantOfProjectOrError(dto.getProjectId(), token);
        Mono<Boolean> doFieldBelongsToProject = projectService.doFieldBelongsToProject(dto.getFieldId(), dto.getProjectId());
        return Mono.zip(userId, isParticipant, doFieldBelongsToProject)
                .flatMap(result -> {
                    return taskRepository.save(dto.toInsertTask())
                            .flatMap(task -> {
                                String query = String.format(ADD_TASK_TO_USER_QUERY, result.getT1(), task.getId());
                                client.sql(query)
                                    .fetch()
                                    .first();
                                return Mono.just(task);
                            });
                });
    }

    private Mono<Boolean> isOpenTaskById(Long id){
        String query = String.format("%s WHERE t.id = %s", CHECK_TASK_VISIBILITY, id);
        return client.sql(query)
                .fetch()
                .first()
                .defaultIfEmpty(new HashMap<>())
                .flatMap(stringObjectMap -> {
                    if(stringObjectMap.isEmpty()) return Mono.error(new NotFoundException("Task was not found!"));
                    return stringObjectMap.get("visibility").equals("OPEN") ?
                            Mono.just(true) :
                            Mono.just(false);
                });
    }

    /*Метод обновления таски: если у пользователя, отправившего запрос, есть такая таска или он админ проекта, то обновляем ее*/
    public Mono<Task> updateTask(TaskDTO dto, String token){
        Mono<Long> userId = userService.findUserByUsername(jwtService.extractUsername(token)).flatMap(user -> Mono.just(user.getId()));
        Mono<Boolean> doFieldBelongsToProject = projectService.doFieldBelongsToProject(dto.getFieldId(), dto.getProjectId());
        Mono<Task> task = taskRepository.findById(dto.getId()).defaultIfEmpty(Task.defaultIfEmpty());
        return Mono.zip(userId, doFieldBelongsToProject, task).flatMap(result1 -> {
            Mono<Boolean> isAdmin = UserService.getUserRoleInProject(dto.getProjectId(), token, jwtService, client)
                    .flatMap(role -> Mono.just(role.equals("ADMIN")));
            Mono<Boolean> isResponsible = isUserResponsibleForTaskOrError(result1.getT1(), dto.getId());
            return Mono.zip(isAdmin, isResponsible).flatMap(result2 -> {
               if(!result1.getT3().isEmpty() && result1.getT2() && (result2.getT1() || result2.getT2())) {
                   return taskRepository.save(result1.getT3().toUpdate(dto));
               }
               return Mono.error(new BadRequestException("Invalid data!"));
            });
        });
    }

    public Mono<Void> deleteTaskById(Long id, Long projectId, String token){
        Mono<Long> userId = userService.findUserByUsername(jwtService.extractUsername(token)).flatMap(user -> Mono.just(user.getId()));
        Mono<Task> task = taskRepository.findById(id).defaultIfEmpty(Task.defaultIfEmpty());
        return Mono.zip(userId, task).flatMap(objects -> {
            Mono<Boolean> isAdmin = UserService.getUserRoleInProject(projectId, token, jwtService, client)
                    .flatMap(role -> Mono.just(role.equals("ADMIN")));
            Mono<Boolean> isResponsible = isUserResponsibleForTaskOrError(objects.getT1(), id);
            return Mono.zip(isAdmin, isResponsible).flatMap(objects1 -> {
                if(!objects.getT2().isEmpty() && (objects1.getT2() || objects1.getT1())){
                    return taskRepository.deleteById(id);
                }
                return Mono.error(new NotFoundException("Task was not found!"));
            });

        });
    }


}
