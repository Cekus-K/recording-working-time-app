drop table if exists working_times;
create table working_times
(
    id         bigserial primary key,
    uuid       uuid      not null unique,
    start_time timestamp not null check (start_time < end_time),
    end_time   timestamp not null check (end_time > start_time),
    user_id    bigint    not null,
    project_id bigint    not null,
    foreign key (user_id) references users (id),
    foreign key (project_id) references projects (id)
);
create index wt_start_time_index on working_times (start_time);
create index wt_end_time_index on working_times (end_time);
create index wt_user_id_index on working_times (user_id);
create index wt_project_id_index on working_times (project_id);
