package com.morning.taskapimain.entity.project;

public enum Permission {
    // 🔹 Доступ к задачам
    CAN_CREATE_TASKS,
    CAN_EDIT_TASKS,
    CAN_DELETE_TASKS,
    CAN_ARCHIVE_TASKS,
    CAN_RESTORE_TASKS,
    CAN_COMMENT_TASKS,
    CAN_MANAGE_REMINDERS,
    CAN_MANAGE_TASK_TAGS,
    CAN_MANAGE_TASK_STATUSES,
    CAN_MANAGE_ASSIGNEES,


    // 🔹 Доступ к проекту
    CAN_EDIT_PROJECT,
    CAN_MANAGE_MEMBERS,
    CAN_MANAGE_ROLES,
}
