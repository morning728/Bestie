package com.morning.taskapimain.controller.project;

import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@CrudExceptionHandler
@AccessExceptionHandler
@BadRequestExceptionHandler
public class ReportController {

    private final ReportService reportService;

    @PostMapping("")
    public Mono<ResponseEntity<String>> createReport(@RequestParam(name = "project_id") Long projectId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        reportService.generateReportAsync(projectId, token); // fire-and-forget
        return Mono.just(ResponseEntity.accepted().body("Report will be created in a few minutes."));
    }
}