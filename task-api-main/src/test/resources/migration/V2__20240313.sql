CREATE TABLE project (
     id         SERIAL PRIMARY KEY,
     name   VARCHAR(64)   NOT NULL,
     visibility   VARCHAR(16)   DEFAULT 'OPEN',
     description VARCHAR(512),
     status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
     created_at TIMESTAMP,
     updated_at TIMESTAMP
);
CREATE TABLE user_project (
      project_id INT,
      user_id   INT,
      PRIMARY KEY (project_id, user_id),
      FOREIGN KEY (project_id) REFERENCES project(id) ON UPDATE CASCADE ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);



INSERT INTO "users" (username, first_name,last_name,status, created_at, updated_at)
VALUES ('admin1', 'AdminName','AdminLastname', 'ACTIVE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
       ('admin2', 'AdminName2','AdminLastname2', 'ACTIVE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO project (name, description, status, created_at, updated_at, visibility)
    VALUES ('test_project1', 'test project description', 'ACTIVE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, 'OPEN'),
           ('test_project2', 'test project description2', 'ACTIVE',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, 'CLOSE');

INSERT INTO user_project (project_id, user_id)
    values (1, 1),(2,1);
