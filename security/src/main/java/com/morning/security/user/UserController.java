package com.morning.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/security/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<?> getProfileInfoByUsername(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                      @RequestParam(name = "username", required = false) String username) {
        return username != null ?
                new ResponseEntity(service.getUserInfoByUsername(username), HttpStatusCode.valueOf(200)) :
                new ResponseEntity(service.getUserInfoByToken(token), HttpStatusCode.valueOf(200));
    }

/*    @GetMapping("/info")
    public ResponseEntity<?> getProfileInfo(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return new ResponseEntity(service.getUserInfoByToken(token), HttpStatusCode.valueOf(200));
    }*/


    @PutMapping("/info")
    public ResponseEntity<?> updateProfileInfo(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                               @RequestBody ProfileInfoDTO dto){
        return new ResponseEntity(service.updateUserInfo(token, dto), HttpStatusCode.valueOf(200));
    }
}
