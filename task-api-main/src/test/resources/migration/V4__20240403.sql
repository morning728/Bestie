ALTER TABLE user_project
drop CONSTRAINT user_project_project_id_fkey;

alter table user_project
    add constraint user_project_project_id_fkey
        foreign key (project_id) references project (id)
            ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE user_project
drop CONSTRAINT user_project_user_id_fkey;

alter table user_project
    add constraint user_project_user_id_fkey
        foreign key (user_id) references users (id)
            ON UPDATE CASCADE ON DELETE CASCADE;