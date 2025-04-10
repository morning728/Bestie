package com.morning.notification.entity.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteEvent {
    private String action;
    private String username;
    private String email;
    private String telegramId;
    private String chatId;
    private String projectTitle;
    private String deletedBy;
}
