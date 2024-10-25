package com.morning.taskapimain.controller.tech;

import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tech")
@CrudExceptionHandler
@AccessExceptionHandler
@BadRequestExceptionHandler
public class TechController {

    private final ProjectService projectService;


    @GetMapping("/accept")
    public Flux<Object> addUserToProject(@RequestParam(name = "data") String acceptationToken){
        return projectService.addUserToProjectByAcceptationToken(acceptationToken);

    }
}
