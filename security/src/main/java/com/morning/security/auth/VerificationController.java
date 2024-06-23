package com.morning.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/security/v1/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final AuthenticationService service;
    @GetMapping("/verify-email")
    public void verifyEmail(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                            @RequestParam(name = "data") String emailToken
    ){
        service.verifyEmail(token, emailToken);
    }
}
