package com.morning.taskapimain.entity.kafka.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteEvent {
    private String action = "INVITE_TO_PROJECT";
    private String username;
    private String email;
    private String telegramId;
    private String chatId;
    private String inviteLink;
    private String projectTitle;
    private String invitedBy;

    public InviteEvent setDefaultAction() {
        this.action = "INVITE_TO_PROJECT";
        return this;
    }
}

