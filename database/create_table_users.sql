drop table if exists users;
create table users
(
    id            bigserial primary key,
    uuid          uuid           not null unique,
    login         varchar(255)   not null unique,
    first_name    varchar(255)   not null,
    last_name     varchar(255)   not null,
    role          varchar(255)   not null check (role in ('ADMIN', 'MANAGER', 'EMPLOYEE')),
    password      varchar(255)   not null,
    email         varchar(254)   not null,
    cost_per_hour numeric(20, 2) not null check (cost_per_hour > 0)
);
create index u_role_index on users (role);
create index u_cost_per_hour_index on users (cost_per_hour);
