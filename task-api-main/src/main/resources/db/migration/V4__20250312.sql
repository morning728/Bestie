CREATE TABLE task_assignee (
                               id SERIAL PRIMARY KEY,
                               task_id BIGINT NOT NULL REFERENCES task(id) ON DELETE CASCADE,
                               user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE
);