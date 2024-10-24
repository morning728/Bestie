CREATE TABLE user_task (
      task_id INT,
      user_id   INT,
      PRIMARY KEY (task_id, user_id),
      FOREIGN KEY (task_id) REFERENCES task(id) ON UPDATE CASCADE ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);