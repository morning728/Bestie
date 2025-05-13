package com.morning.notification.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ParsedReminder {
    private Long taskId;
    private String taskName;
    private String projectName;
    private String reminderText;
    private LocalDateTime remindAt;
    private List<String> usernames;
}
