CREATE TABLE app_user (
   id         SERIAL PRIMARY KEY,
   username   VARCHAR(64)   NOT NULL UNIQUE,
   -- password   VARCHAR(2048) NOT NULL,
   first_name VARCHAR(64),
   last_name  VARCHAR(64) ,
   status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
   created_at TIMESTAMP,
   updated_at TIMESTAMP
);

CREATE TABLE project (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    color VARCHAR(7) DEFAULT '#9932CC',
    icon VARCHAR(10) DEFAULT '📁',
    priority VARCHAR(20) DEFAULT 'Medium',
    status VARCHAR(20) DEFAULT 'Planned',
    deadline DATE,
    owner_id INT REFERENCES app_user(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Таблица ролей проекта (каждый проект может иметь свои роли)
CREATE TABLE project_role (
                              id SERIAL PRIMARY KEY,
                              project_id INT REFERENCES project(id) ON DELETE CASCADE,
                              name VARCHAR(50) NOT NULL,
                              permissions JSONB NOT NULL DEFAULT '{}'::JSONB,  -- 🔥 Хранение разрешений в JSONB
                              UNIQUE (project_id, name)
);
CREATE TABLE user_project (
    id SERIAL PRIMARY KEY,
    project_id INT REFERENCES project(id) ON DELETE CASCADE,
    user_id INT REFERENCES app_user(id) ON DELETE CASCADE,
    role_id INT REFERENCES project_role(id) ON DELETE SET NULL,
    UNIQUE (project_id, user_id)
);

CREATE TABLE project_resource (
    id SERIAL PRIMARY KEY,
    project_id INT REFERENCES project(id) ON DELETE CASCADE,
    url TEXT NOT NULL,
    description TEXT
);

CREATE TABLE project_status (
    id SERIAL PRIMARY KEY,
    project_id INT REFERENCES project(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7) DEFAULT '#808080',
    UNIQUE (project_id, name)
);

-- Таблица тегов (уникальные теги для каждого проекта)
CREATE TABLE project_tag (
    id SERIAL PRIMARY KEY,
    project_id INT REFERENCES project(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7) DEFAULT '#008000',
    UNIQUE (project_id, name)
);

-- Таблица задач
CREATE TABLE task (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status_id INT REFERENCES project_status(id) ON DELETE SET NULL,
    priority VARCHAR(20) DEFAULT 'Medium',
    start_date DATE,
    end_date DATE,
    start_time TIME DEFAULT '00:00',
    end_time TIME DEFAULT '23:59',
    is_archived BOOLEAN DEFAULT FALSE,
    archived_at TIMESTAMP NULL, -- ✅ Дата, когда задача была заархивирована
    project_id INT REFERENCES project(id) ON DELETE CASCADE,
    created_by INT REFERENCES app_user(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица для связи задач и тегов (многие ко многим)
CREATE TABLE task_tag (
    id SERIAL PRIMARY KEY,
    task_id INT REFERENCES task(id) ON DELETE CASCADE,
    tag_id INT REFERENCES project_tag(id) ON DELETE CASCADE,
    UNIQUE (task_id, tag_id)
);

-- Таблица комментариев к задачам
CREATE TABLE task_comment (
    id SERIAL PRIMARY KEY,
    task_id INT REFERENCES task(id) ON DELETE CASCADE,
    user_id INT REFERENCES app_user(id) ON DELETE SET NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица напоминаний о задачах
CREATE TABLE task_reminder (
    id SERIAL PRIMARY KEY,
    task_id INT REFERENCES task(id) ON DELETE CASCADE,
    reminder_date DATE,
    reminder_time TIME
);






