drop table if exists working_times;
create table working_times
(
    id         bigserial primary key,
    start_time timestamp not null check (start_time < end_time),
    end_time   timestamp not null check (end_time > start_time),
    user_id    bigint    not null,
    project_id bigint    not null,
    foreign key (user_id) references users (id),
    foreign key (project_id) references projects (id)
)
