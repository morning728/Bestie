package com.morning.taskapimain.controller.user;

import com.morning.taskapimain.entity.User;
import com.morning.taskapimain.entity.dto.ProfileDTO;
import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.entity.dto.UserDTO;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.mapper.ProjectMapper;
import com.morning.taskapimain.mapper.UserMapper;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.UserService;
import com.morning.taskapimain.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@CrudExceptionHandler
public class UserController {
    private final UserService userService;
    private final ProjectService projectService;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;
    private final JwtService jwtService;


    @GetMapping("")
    public Flux<UserDTO> getUserByUsernameContains(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                 @RequestParam(name = "contains", required = false) String substring){
        return userService.findUsersByUsernameContains(substring).map(userMapper::map);
    }

    @GetMapping("/me")
    public Mono<UserDTO> getMeByToken(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                   @RequestParam(required = false, name = "isManager") Long projectId){
        return projectId == null ?
                userService.getUserByToken(token).map(userMapper::map) :
                userService.getUserByTokenWithCheckingIsManager(token, projectId);
    }

    @GetMapping("/projects")
    public Flux<ProjectDTO> getUserProjects(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        String username = jwtService.extractUsername(token);
        return projectService.findAllByUsername(username).map(projectMapper::map);
    }

    @PutMapping("")
    public Mono<ResponseEntity<String>> updateUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                           @RequestBody ProfileDTO dto){
        return userService.updateProfileByToken(token, dto)
                .thenReturn((new ResponseEntity<>("User was updated!", HttpStatus.OK)))
                .onErrorReturn(new ResponseEntity<>("User was not updated!", HttpStatus.BAD_REQUEST));
    }


}
