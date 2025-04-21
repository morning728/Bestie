CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,         -- Username как ключ
    email_enabled BOOLEAN DEFAULT TRUE,            -- Получать уведомления на email
    telegram_enabled BOOLEAN DEFAULT TRUE,         -- Получать уведомления в Telegram
    invite_via_email BOOLEAN DEFAULT TRUE,         -- Получать приглашения по email
    invite_via_telegram BOOLEAN DEFAULT TRUE,      -- Получать приглашения в Telegram
    task_assigned_enabled BOOLEAN DEFAULT TRUE,
    task_updated_enabled BOOLEAN DEFAULT TRUE,
    task_deadline_reminder BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


