INSERT INTO app_user (id, username, first_name, last_name, status, created_at, updated_at) VALUES
(1,'admin', 'Admin', 'User', 'ACTIVE', NOW(), NOW()),
(2,'john_doe', 'John', 'Doe', 'ACTIVE', NOW(), NOW()),
(3,'jane_doe', 'Jane', 'Doe', 'ACTIVE', NOW(), NOW()),
(4,'alice_smith', 'Alice', 'Smith', 'ACTIVE', NOW(), NOW()),
(5,'bob_jones', 'Bob', 'Jones', 'ACTIVE', NOW(), NOW());
INSERT INTO project (id, title, description, color, icon, priority, status, deadline, owner_id, created_at, updated_at) VALUES
(1,'Web App Development', 'Developing a new web application.', '#FF5733', '🚀', 'High', 'In Progress', '2025-12-31', 1, NOW(), NOW()),
(2,'Marketing Strategy', 'Plan and execute a new marketing campaign.', '#33FF57', '📊', 'Medium', 'Planned', '2025-10-01', 2, NOW(), NOW()),
(3,'Mobile App', 'Building a new mobile application.', '#3357FF', '📱', 'Critical', 'On Hold', '2026-01-15', 3, NOW(), NOW());
-- ✅ Пример данных (создаем роли с разрешениями)
INSERT INTO project_role (id, project_id, name, permissions) VALUES
(1, 1, 'Owner', '{
    "CAN_CREATE_TASKS": true,
    "CAN_EDIT_TASKS": true,
    "CAN_DELETE_TASKS": true,
    "CAN_ARCHIVE_TASKS": true,
    "CAN_RESTORE_TASKS": true,
    "CAN_COMMENT_TASKS": true,
    "CAN_ADD_REMINDERS": true,
    "CAN_EDIT_PROJECT": true,
    "CAN_MANAGE_MEMBERS": true,
    "CAN_MANAGE_ROLES": true
}'),
(2, 1, 'Manager', '{
    "CAN_CREATE_TASKS": true,
    "CAN_EDIT_TASKS": true,
    "CAN_DELETE_TASKS": true,
    "CAN_ARCHIVE_TASKS": true,
    "CAN_RESTORE_TASKS": false,
    "CAN_COMMENT_TASKS": true,
    "CAN_ADD_REMINDERS": true,
    "CAN_EDIT_PROJECT": true,
    "CAN_MANAGE_MEMBERS": true,
    "CAN_MANAGE_ROLES": false
}'),
(3, 1, 'Developer', '{
    "CAN_CREATE_TASKS": true,
    "CAN_EDIT_TASKS": true,
    "CAN_DELETE_TASKS": false,
    "CAN_ARCHIVE_TASKS": false,
    "CAN_RESTORE_TASKS": false,
    "CAN_COMMENT_TASKS": true,
    "CAN_ADD_REMINDERS": true,
    "CAN_EDIT_PROJECT": false,
    "CAN_MANAGE_MEMBERS": false,
    "CAN_MANAGE_ROLES": false
}'),
(4, 1, 'Viewer', '{
    "CAN_CREATE_TASKS": false,
    "CAN_EDIT_TASKS": false,
    "CAN_DELETE_TASKS": false,
    "CAN_ARCHIVE_TASKS": false,
    "CAN_RESTORE_TASKS": false,
    "CAN_COMMENT_TASKS": true,
    "CAN_ADD_REMINDERS": false,
    "CAN_EDIT_PROJECT": false,
    "CAN_MANAGE_MEMBERS": false,
    "CAN_MANAGE_ROLES": false
}');
INSERT INTO user_project (id, project_id, user_id, role_id) VALUES
(1, 1, 2, 2), -- John Doe (Developer) в Web App Development
(2, 1, 3, 3), -- Jane Doe (Viewer) в Web App Development
(3, 2, 4, 4), -- Alice Smith (Marketing Lead) в Marketing Strategy
(4, 2, 5, 4); -- Bob Jones (Analyst) в Marketing Strategy
INSERT INTO project_tag (id, project_id, name, color) VALUES
(1, 1, 'Frontend', '#FF5733'),
(2, 1, 'Backend', '#33FF57'),
(3, 2, 'SEO', '#FFC300'),
(4, 2, 'Social Media', '#DAF7A6');
INSERT INTO project_status (id, project_id, name, color) VALUES
(1, 1, 'To Do', '#808080'),
(2, 1, 'In Progress', '#FF5733'),
(3, 1, 'Completed', '#33FF57'),
(4, 2, 'Planned', '#FFC300'),
(5, 2, 'Executing', '#DAF7A6');
INSERT INTO task (id, title, description, status_id, priority, start_date, end_date, start_time, end_time, is_archived, archived_at, project_id, created_by, created_at, updated_at) VALUES
(1, 'Design Homepage', 'Create the UI/UX design for the homepage.', 2, 'High', '2025-09-01', '2025-09-10', '09:00', '17:00', FALSE, NULL, 1, 2, NOW(), NOW()),
(2, 'Setup Database', 'Configure PostgreSQL database and tables.', 2, 'Critical', '2025-09-05', '2025-09-15', '10:00', '18:00', FALSE, NULL, 1, 2, NOW(), NOW()),
(3, 'Keyword Research', 'Analyze top keywords for SEO.', 3, 'Medium', '2025-08-01', '2025-08-07', '08:30', '16:00', FALSE, NULL, 2, 4, NOW(), NOW()),
(4, 'Ad Campaign Planning', 'Set up Google and Facebook ad campaigns.', 4, 'High', '2025-08-10', '2025-08-20', '10:00', '17:00', FALSE, NULL, 2, 5, NOW(), NOW());
INSERT INTO task_tag (id, task_id, tag_id) VALUES
(1, 1, 1), -- Design Homepage -> Frontend
(2, 2, 2), -- Setup Database -> Backend
(3, 3, 3), -- Keyword Research -> SEO
(4, 4, 4); -- Ad Campaign Planning -> Social Media
INSERT INTO task_comment (id, task_id, user_id, comment, created_at) VALUES
(1, 1, 3, 'Make sure to use modern UI elements.', NOW()),
(2, 2, 2, 'Use relational tables for better performance.', NOW()),
(3, 3, 4, 'Research top trending keywords in 2025.', NOW()),
(4, 4, 5, 'Target audience should be between 25-35 years old.', NOW());
INSERT INTO task_reminder (id, task_id, reminder_date, reminder_time) VALUES
(1, 1, '2025-09-09', '08:00'),
(2, 2, '2025-09-14', '09:00'),
(3, 3, '2025-08-06', '07:30'),
(4, 4, '2025-08-19', '10:00');
