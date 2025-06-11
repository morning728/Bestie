package com.morning.taskapimain.service.report;

import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.entity.dto.TaskDTO;
import com.morning.taskapimain.entity.user.Contacts;
import com.morning.taskapimain.repository.ProjectUserRepository;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.TaskService;
import com.morning.taskapimain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectUserRepository projectUserRepository;
    private final UserService userService;
    private final ReportBuilder reportBuilder;
    @Qualifier("webClient")
    private final WebClient webClient;

    @Async
    public void generateReportAsync(Long projectId, String token) {
        Mono.zip(
                        projectService.getFullProjectInfoById(projectId),
                        taskService.getTasksByProject(projectId).flatMap(task -> taskService.getFullTaskInfoById(task.getId())).collectList(),
                        projectUserRepository.findUsersByProjectId(projectId).flatMap(user -> userService.findContactsByUsernameWithWebClient(user.getUsername(), token)).collectList()
                )
                .flatMap(tuple -> {
                    ProjectDTO projectDTO = tuple.getT1();
                    List<TaskDTO> taskDTOs = tuple.getT2();
                    List<Contacts> membersContacts = tuple.getT3();

                    byte[] pdfBytes = null;
                    try {
                        pdfBytes = reportBuilder.buildReport(projectDTO, taskDTOs, membersContacts);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                    String filename = String.format("%s_REPORT_%s.pdf", projectDTO.getTitle(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss")).toString());
                    return sendReportToAttachmentService(projectId, pdfBytes, filename, token);
                })
                .doOnSuccess(ignored -> log.info("✅ Отчёт для проекта ID={} успешно создан и отправлен", projectId))
                .doOnError(e -> log.error("❌ Ошибка при генерации отчета по проекту ID=" + projectId, e))
                .subscribe();
    }

    public Mono<Void> sendReportToAttachmentService(Long projectId, byte[] fileContent, String fileName, String token) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", fileContent)
                .filename(fileName)
                .contentType(MediaType.APPLICATION_PDF);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/file-api/v1/attachments/reports/{projectId}")
                        .build(projectId))
                .header("Authorization",  token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(unused -> log.info("📤 Отчет успешно отправлен в attachment-сервис"))
                .doOnError(e -> log.error("❌ Ошибка при отправке отчета: {}", e.getMessage()));
    }
}
