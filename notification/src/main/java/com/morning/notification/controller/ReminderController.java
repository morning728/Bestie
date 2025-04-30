package com.morning.notification.controller;


import com.morning.notification.entity.task.ReminderDTO;
import com.morning.notification.entity.user.Contacts;
import com.morning.notification.entity.user.NotificationPreferences;
import com.morning.notification.entity.user.NotificationPreferencesDTO;
import com.morning.notification.service.UserService;
import com.morning.notification.service.quartz.TaskSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification/v1/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final TaskSchedulerService taskSchedulerService;

    @GetMapping("/{taskId}")
    public ResponseEntity<ReminderDTO> getReminderInfo(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskSchedulerService.getReminderInfo(taskId));
    }
}

