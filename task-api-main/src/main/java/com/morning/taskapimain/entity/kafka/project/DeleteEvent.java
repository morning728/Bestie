package com.morning.taskapimain.entity.kafka.project;

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
    private String projectTitle;
    private String deletedBy;

    public DeleteEvent setDefaultAction() {
        this.action = "DELETE_FROM_PROJECT";
        return this;
    }
}
