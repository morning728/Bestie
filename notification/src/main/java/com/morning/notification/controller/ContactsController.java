package com.morning.notification.controller;

import com.morning.notification.entity.user.Contacts;
import com.morning.notification.entity.user.NotificationPreferences;
import com.morning.notification.entity.user.NotificationPreferencesDTO;
import com.morning.notification.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notification/v1/contacts")
@RequiredArgsConstructor
public class ContactsController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<Contacts> getContactsByUsername(@RequestParam(name = "username") String username) {
        return new ResponseEntity<>(userService.getContactsByUsername(username), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreferences> getContactsWithPreferencesByUsername(@RequestParam(name = "username") String username) {
        return new ResponseEntity<>(userService.getNotificationPreferencesByUsername(username), HttpStatusCode.valueOf(200));
    }

    @PostMapping("/preferences")
    public NotificationPreferences updatePreferences(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
            @RequestBody NotificationPreferencesDTO notificationPreferencesDTO
    ){
        return userService.updatePreferences(token, notificationPreferencesDTO);
    }

    @GetMapping("/verify-email")
    public void verifyEmail(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(name = "token") String emailToken
    ){
        userService.verifyEmail(token, emailToken);
    }

    @GetMapping("/email")
    public void updateEmail(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(name = "new-email") String newEmail
    ){
        userService.setEmail(token, newEmail);
    }

}
