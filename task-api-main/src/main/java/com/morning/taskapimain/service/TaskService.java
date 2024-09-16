package com.morning.taskapimain.service;

import com.morning.taskapimain.entity.Contacts;
import com.morning.taskapimain.entity.Task;
import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.exception.AccessException;
import com.morning.taskapimain.exception.BadRequestException;
import com.morning.taskapimain.exception.NotFoundException;
import com.morning.taskapimain.repository.TaskRepository;
import com.morning.taskapimain.service.kafka.KafkaNotificationService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final DatabaseClient client;
    private final ProjectService projectService;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final JwtService jwtService;
    private final KafkaNotificationService kafkaNotificationService;
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

    private static final String FIND_RESPONSIBLE_FOR_TASK_QUERY = """
            SELECT u.username
            FROM users u
            JOIN user_task ut ON u.id = ut.user_id
            WHERE ut.task_id = %s;
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
    public Mono<Boolean> isUserResponsibleForTask(Long userId, Long taskId) {
        String query = String.format(IS_USER_RESPONSIBLE_QUERY, userId, taskId);

        return client.sql(query)
                .fetch()
                .first()
                .flatMap(stringObjectMap -> {
                    log.info(stringObjectMap.toString());
                    return !stringObjectMap.isEmpty() ? Mono.just(true) : Mono.just(false);
                })
                // Если результат пустой (нет данных), вернем false
                .defaultIfEmpty(false)
                // Если возникла ошибка, вернем false
                .onErrorResume(throwable -> {
                    log.info(throwable.toString());
                    return Mono.just(false);
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

    public Flux<TaskDTO> getTasksByProjectId(Long projectId, String token){
        return projectService.hasAccessToProjectOrError(projectId, token)
                .thenMany(taskRepository.findTasksByProjectIdOrderById(projectId))
                .flatMap(task -> getListOfResponsibleUsers(task.getId())
                        .map(responsibleUsers -> TaskDTO.toTaskDTO(task, responsibleUsers)));
    }

    public Flux<TaskDTO> getTasksByProjectIdAndFieldId(Long projectId, Long fieldId, String token) {
        // Проверяем доступ к проекту
        return projectService.hasAccessToProjectOrError(projectId, token)
                // Получаем задачи по проекту и полю
                .thenMany(taskRepository.findTasksByProjectIdAndFieldIdOrderById(projectId, fieldId))
                // Преобразуем каждую задачу в TaskDTO, добавляя ответственных пользователей
                .flatMap(task -> getListOfResponsibleUsers(task.getId())
                        .map(responsibleUsers -> TaskDTO.toTaskDTO(task, responsibleUsers)));
    }

    // Метод для получения списка ответственных пользователей по taskId
    private Mono<List<String>> getListOfResponsibleUsers(Long taskId) {
        String query = String.format(FIND_RESPONSIBLE_FOR_TASK_QUERY, taskId);
        return client.sql(query)
                .fetch()
                .all()
                .map(result -> (String) result.get("username"))
                .collectList();  // Преобразуем результат в список
    }


    public Mono<Task> addTask(TaskDTO dto, String token){
        Mono<Long> userIdMono = userService.findUserByUsername(jwtService.extractUsername(token)).flatMap(user -> Mono.just(user.getId()));
        Mono<Boolean> isParticipantMono = projectService.isParticipantOfProjectOrError(dto.getProjectId(), token);
        Mono<Boolean> doFieldBelongsToProjectMono = projectService.doFieldBelongsToProject(dto.getFieldId(), dto.getProjectId());
        Mono<Contacts> contactsMono = userService.getUserContactsByUsername(jwtService.extractUsername(token));
        return Mono.zip(userIdMono, isParticipantMono, doFieldBelongsToProjectMono, contactsMono)
                .flatMap(result -> {
                    Long userId = result.getT1();
                    Contacts contacts = result.getT4();
                    return taskRepository.save(dto.toInsertTask())
                            .flatMap(task -> {
                                kafkaNotificationService.sendTaskCreation(
                                        task.getId(),
                                        task.getName(),
                                        List.of(contacts.getEmail()),
                                        task.getFinishDate()
                                ).subscribe();
                                String query = String.format(ADD_TASK_TO_USER_QUERY, userId, task.getId());
                                return client.sql(query)
                                    .fetch()
                                    .first()
                                    .then(Mono.just(task));
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
    public Mono<Task> updateTask(TaskDTO dto, String token) {
        // Получаем идентификатор пользователя по токену
        Mono<Long> userIdMono = userService.findUserByUsername(jwtService.extractUsername(token))
                .map(user -> user.getId());

        // Проверяем, принадлежит ли поле проекту
        Mono<Boolean> fieldBelongsToProjectMono = projectService.doFieldBelongsToProject(dto.getFieldId(), dto.getProjectId());

        // Ищем задачу по её идентификатору
        Mono<Task> taskMono = taskRepository.findById(dto.getId())
                .defaultIfEmpty(Task.defaultIfEmpty());  // Возвращаем пустой объект, если задача не найдена

        // Объединяем данные: userId, проверка на принадлежность поля к проекту, и задача
        return Mono.zip(userIdMono, fieldBelongsToProjectMono, taskMono)
                .flatMap(tuple -> {
                    Long userId = tuple.getT1();
                    Boolean fieldBelongsToProject = tuple.getT2();
                    Task task = tuple.getT3();

                    // Проверка на наличие задачи и принадлежность поля к проекту
                    if (task.isEmpty()) {
                        return Mono.error(new NotFoundException("Task not found!"));
                    }

                    if (!fieldBelongsToProject) {
                        return Mono.error(new BadRequestException("Field does not belong to the project!"));
                    }

                    // Проверяем, является ли пользователь администратором проекта
                    Mono<Boolean> isAdminMono = UserService.getUserRoleInProject(dto.getProjectId(), token, jwtService, client)
                            .map(role -> role.equals("ADMIN"));

                    // Проверяем, является ли пользователь ответственным за задачу
                    Mono<Boolean> isResponsibleMono = isUserResponsibleForTask(userId, dto.getId());

                    // Объединяем проверки на администратора и ответственность
                    return Mono.zip(isAdminMono, isResponsibleMono)
                            .flatMap(roleCheck -> {
                                Boolean isAdmin = roleCheck.getT1();
                                Boolean isResponsible = roleCheck.getT2();

                                // Если пользователь — админ или ответственен за задачу, обновляем задачу
                                if (isAdmin || isResponsible) {
                                    return taskRepository.save(task.toUpdate(dto));  // Обновляем задачу
                                }

                                return Mono.error(new BadRequestException("You do not have permission to update this task!"));
                            });
                });
    }


/*    public Mono<Void> deleteTaskById(Long id, Long projectId, String token){
        Mono<Long> userId = userService.findUserByUsername(jwtService.extractUsername(token)).flatMap(user -> Mono.just(user.getId()));
        Mono<Task> task = taskRepository.findById(id).defaultIfEmpty(Task.defaultIfEmpty());
        return Mono.zip(userId, task).flatMap(objects -> {
            Mono<Boolean> isAdmin = UserService.getUserRoleInProject(projectId, token, jwtService, client)
                    .flatMap(role -> Mono.just(role.equals("ADMIN")));
            Mono<Boolean> isResponsible = isUserResponsibleForTask(objects.getT1(), id);
            log.info("asda");
            return Mono.zip(isAdmin, isResponsible).flatMap(objects1 -> {
                log.info("adsadsda");
                if(!objects.getT2().isEmpty() && (objects1.getT2() || objects1.getT1())){
                    return taskRepository.deleteById(id);
                }
                return Mono.error(new NotFoundException("Task was not found!"));
            });

        });
    }*/
public Mono<Void> deleteTaskById(Long id, Long projectId, String token) {
    Mono<Long> userId = userService.findUserByUsername(jwtService.extractUsername(token))
            .flatMap(user -> Mono.just(user.getId()));

    Mono<Task> task = taskRepository.findById(id)
            .defaultIfEmpty(Task.defaultIfEmpty());  // Обработка пустого задания

    return Mono.zip(userId, task)
            .flatMap(objects -> {
                Long foundUserId = objects.getT1();
                Task foundTask = objects.getT2();

                if (foundTask.isEmpty()) {
                    return Mono.error(new NotFoundException("Task was not found!"));
                }

                // Получаем информацию об администраторе
                Mono<Boolean> isAdmin = UserService.getUserRoleInProject(projectId, token, jwtService, client)
                        .map(role -> role.equals("ADMIN"));

                // Проверяем, ответственен ли пользователь за задачу
                Mono<Boolean> isResponsible = isUserResponsibleForTask(foundUserId, id);

                // Ждем завершения обеих проверок
                return Mono.zip(isAdmin, isResponsible)
                        .flatMap(results -> {
                            Boolean isAdminResult = results.getT1();
                            Boolean isResponsibleResult = results.getT2();

                            log.info("isAdmin: {}, isResponsible: {}", isAdminResult, isResponsibleResult);

                            if (isAdminResult || isResponsibleResult) {
                                // Удаляем задачу, если админ или ответственен
                                return taskRepository.deleteById(id);
                            } else {
                                return Mono.error(new NotFoundException("You don't have permissions to delete this task!"));
                            }
                        });
            });
}


}
