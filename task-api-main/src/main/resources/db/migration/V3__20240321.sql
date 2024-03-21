CREATE TABLE field (
    id  SERIAL PRIMARY KEY,
    name   VARCHAR(64)   NOT NULL,
    project_id INT,
    FOREIGN KEY (project_id) REFERENCES project(id)
);

CREATE TABLE task (
    id  SERIAL PRIMARY KEY,
    name   VARCHAR(64)   NOT NULL,
    description VARCHAR(512),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    project_id INT,
    field_id INT,
    FOREIGN KEY (project_id) REFERENCES project(id),
    FOREIGN KEY (field_id) REFERENCES field(id)
);