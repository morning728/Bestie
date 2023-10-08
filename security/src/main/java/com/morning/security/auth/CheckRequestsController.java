package com.morning.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security/v1/validate")
public class CheckRequestsController {


    @GetMapping("/user")
    public String checkUser() {
        return "Access is allowed!";
    }

    @GetMapping("/admin")
    public String checkAdmin() {
        return "Access is allowed!";
    }

    @GetMapping("/manager")
    public String checkManager() {
        return "Access is allowed!";
    }

}