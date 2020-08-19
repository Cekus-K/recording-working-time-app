drop table if exists users_projects;
create table users_projects
(
    users_id    bigserial not null,
    projects_id bigserial not null,
    primary key (users_id, projects_id),
    foreign key (users_id) references users (id) on update cascade on delete cascade,
    foreign key (projects_id) references projects (id) on update cascade
);
create index up_user_id_index on users_projects (users_id);
create index up_project_id_index on users_projects (projects_id);
