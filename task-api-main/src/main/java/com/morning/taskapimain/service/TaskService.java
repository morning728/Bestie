package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.Task;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.exception.BadRequestException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.TaskRepository;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
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
    private final TaskRepository taskRepository;
    private final JwtService jwtService;
    private static final String SELECT_QUERY =     """
    select t.id, t.name, t.description, t.status, t.created_at, t.updated_at, t.project_id, t.field_id from task  as t
    join project on t.project_id=project.id
    join field on t.field_id=field.id
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
/*        String username = jwtService.extractUsername(token);
        Mono<Boolean> belongsToProject = projectService.isFieldBelongsToProject(dto.getFieldId(), dto.getProjectId());
        Flux<Long> longFlux = projectService.findAllByUsername(username).map(Project::getId)
                .filter(aLong -> {return aLong.equals(dto.getProjectId());});
        return belongsToProject.flatMap(aBoolean -> {
            if(aBoolean){
                return longFlux.singleOrEmpty().flatMap(userProjectsId -> {
                    if(userProjectsId.equals(dto.getProjectId())){
                        return taskRepository.save(dto.toInsertTask());
                    }
                    return Mono.error(new AccessException("U r not project owner!"));
                });
            }
            return Mono.error(new AccessException("U r not project owner!"));
        });*/
        return projectService.isParticipantOfProjectOrError(dto.getProjectId(), token)
                .flatMap(hasAccess -> projectService.isFieldBelongsToProject(dto.getFieldId(), dto.getProjectId())
                        .flatMap(belongs ->{
                            if(belongs){
                                return taskRepository.save(dto.toInsertTask());
                            } else {
                                return Mono.error(new BadRequestException("Bad Request! Check data and try again!"));
                            }
                        }));
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

    /*Метод обновления таски: если у пользователя, отправившего запрос, есть такая таска, то обновляем ее*/
    public Mono<Task> updateTask(TaskDTO dto, String token){
        /*String username = jwtService.extractUsername(token);
        return findByIdAndUsername(dto.getId(), username)
                .defaultIfEmpty(Task.defaultIfEmpty())
                .flatMap(task -> {
            if(!task.isEmpty()){
                task.toUpdate(dto);
                return taskRepository.save(task);
            }
            return Mono.error(new NotFoundException("Task was not found!"));
        });*/
        return projectService.isParticipantOfProjectOrError(dto.getProjectId(), token)
                .then(
                        projectService.isFieldBelongsToProject(dto.getFieldId(), dto.getProjectId())
                                .flatMap(belongs -> {
                                    if(belongs){
                                        return taskRepository.findById(dto.getId())
                                                .defaultIfEmpty(Task.defaultIfEmpty())
                                                .flatMap(task -> {
                                                    if(!task.isEmpty()){
                                                        return taskRepository.save(task.toUpdate(dto));
                                                    }
                                                    return Mono.error(new NotFoundException("Task was not found!"));
                                                });
                                    }
                                    return Mono.error(new BadRequestException("Bad Request! Check data and try again!"));
                                })
                );
    }

    public Mono<Void> deleteTaskById(Long id, Long projectId, String token){
/*        String username = jwtService.extractUsername(token);
        return findByIdAndUsername(id, username)
                .defaultIfEmpty(Task.defaultIfEmpty())
                .flatMap(task -> {
                    if(!task.isEmpty()){
                        return taskRepository.deleteById(id);
                    }
                    return Mono.error(new NotFoundException("Task was not found!"));
        });*/
        return projectService.hasAccessToProjectOrError(projectId, token)
                .then(taskRepository.findById(id).defaultIfEmpty(Task.defaultIfEmpty())
                        .flatMap(task -> {
                            if(!task.isEmpty()){
                                return taskRepository.deleteById(id);
                            }
                            return Mono.error(new NotFoundException("Task was not found!"));
                        }));
    }


}
