drop table if exists projects;
create table projects
(
    id           bigserial primary key,
    uuid         uuid           not null unique,
    project_name varchar(255)   not null unique,
    description  varchar(255),
    start_date   date           not null check (start_date < end_date),
    end_date     date           not null check (end_date > start_date),
    budget       numeric(20, 2) not null check (budget > 0)
);
create index p_start_date on projects (start_date);
create index p_end_date on projects (end_date);
