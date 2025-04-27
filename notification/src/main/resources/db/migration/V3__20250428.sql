-- Переименование столбца task_deadline_reminder в task_reminder
ALTER TABLE notification_preferences
    RENAME COLUMN task_deadline_reminder TO task_reminder;