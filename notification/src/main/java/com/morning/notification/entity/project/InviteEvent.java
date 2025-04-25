package com.morning.notification.entity.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteEvent {
    private String action;
    private String username;
    private String inviteLink;
    private String projectTitle;
    private String invitedBy;
}
