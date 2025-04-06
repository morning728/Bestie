package com.morning.taskapimain.controller.project;

import com.morning.taskapimain.entity.dto.InvitationDTO;
import com.morning.taskapimain.exception.annotation.AccessExceptionHandler;
import com.morning.taskapimain.exception.annotation.BadRequestExceptionHandler;
import com.morning.taskapimain.exception.annotation.CrudExceptionHandler;
import com.morning.taskapimain.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/invitation")
@RequiredArgsConstructor
@CrudExceptionHandler
@AccessExceptionHandler
@BadRequestExceptionHandler
public class InvitationController {
    private final ProjectService projectService;

    @PostMapping("/generate")
    public Mono<ResponseEntity<String>> generateInvite(
            @RequestBody InvitationDTO invitationDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        return projectService.generateInviteToken(
                        token,
                        invitationDTO.getProjectId(),
                        invitationDTO.getUsername(),
                        invitationDTO.getRoleId()
                ).map(ResponseEntity::ok);
    }

    @PostMapping("/generate-all")
    public Mono<ResponseEntity<String>> generateInviteAll(
            @RequestBody InvitationDTO invitationDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        return projectService.generateInviteAllToken(
                token,
                invitationDTO.getProjectId(),
                invitationDTO.getRoleId()
        ).map(ResponseEntity::ok);
    }

    @PostMapping("/accept")
    public Mono<ResponseEntity<Void>> acceptInvite(@RequestParam(name = "token") String invitationToken,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.processInviteToken(invitationToken, token)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/accept-all")
    public Mono<ResponseEntity<Void>> acceptInviteAll(@RequestParam(name = "token") String invitationToken,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return projectService.processInviteAllToken(invitationToken, token)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/invite-directly")
    public Mono<Void> inviteDirectly(
            @RequestBody InvitationDTO invitationDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {
        return projectService.inviteDirectly(
                token,
                invitationDTO.getProjectId(),
                invitationDTO.getUsername(),
                invitationDTO.getRoleId()
        );
    }
}
