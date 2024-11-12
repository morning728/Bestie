
ALTER TABLE user_project
ALTER role SET DEFAULT 'GUEST';

UPDATE user_project SET role = 'ADMIN' WHERE project_id = 1 and user_id = 1;
UPDATE user_project SET role = 'GUEST' WHERE project_id = 2 and user_id = 1;
UPDATE user_project SET role = 'ADMIN' WHERE project_id = 3 and user_id = 3;
UPDATE user_project SET role = 'GUEST' WHERE project_id = 4 and user_id = 3;