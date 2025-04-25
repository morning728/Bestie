CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,         -- Username как ключ
    chat_id BIGINT,
    telegram_id VARCHAR(50),
    email VARCHAR(50),
    email_verified BOOLEAN DEFAULT FALSE,
    email_notification BOOLEAN DEFAULT TRUE,
    telegram_notification BOOLEAN DEFAULT TRUE,
    invite_enabled BOOLEAN DEFAULT TRUE,         -- Получать приглашения по email
    task_assigned_enabled BOOLEAN DEFAULT TRUE,
    task_updated_enabled BOOLEAN DEFAULT TRUE,
    task_deadline_reminder BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


