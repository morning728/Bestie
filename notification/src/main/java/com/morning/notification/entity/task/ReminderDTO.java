package com.morning.notification.entity.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {
    private Boolean reminder;
    private String reminderText;
    private String reminderDate;
    private String reminderTime;
}
