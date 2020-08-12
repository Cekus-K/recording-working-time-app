drop table if exists projects;
create table projects
(
    id           bigserial primary key,
    project_name varchar(255) not null unique,
    description  varchar(255),
    start_date   date         not null check (start_date < end_date),
    end_date     date         not null check (end_date > start_date),
    budget       float        not null check (budget > 0)
)
