package com.morning.openrequesthandler.controller;

import com.morning.openrequesthandler.exception.BadRequestException;
import com.morning.openrequesthandler.exception.annotation.BadRequestExceptionHandler;
import com.morning.openrequesthandler.service.ProjectService;
import com.morning.openrequesthandler.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@BadRequestExceptionHandler
@RequiredArgsConstructor
@RequestMapping("/request-handler/v1/email/")
public class UserController {
    private final UserService userService;

    @GetMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam(name = "data", required = true) String acceptationToken) throws BadRequestException {
        userService.verifyEmail(acceptationToken);
        return  ResponseEntity.status(HttpStatusCode.valueOf(200)).build();
    }
}
