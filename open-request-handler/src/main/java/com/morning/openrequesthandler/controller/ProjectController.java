package com.morning.openrequesthandler.controller;

import com.morning.openrequesthandler.exception.BadRequestException;
import com.morning.openrequesthandler.exception.annotation.BadRequestExceptionHandler;
import com.morning.openrequesthandler.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@Slf4j
@BadRequestExceptionHandler
@RequiredArgsConstructor
@RequestMapping("/request-handler/v1/email/")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/accept-invitation")
    public ResponseEntity addUserToProject(@RequestParam(name = "data", required = true) String acceptationToken) throws BadRequestException {
        projectService.addUserToProjectByAcceptationToken(acceptationToken);
        return  ResponseEntity.status(HttpStatusCode.valueOf(200)).build();
    }
}
