create or replace view projects_view as
select p.id                                    as id,
       p.uuid                                  as uuid,
       p.project_name                          as project_name,
       p.description                           as description,
       p.start_date                            as start_date,
       p.end_date                              as end_date,
       p.budget                                as budget,
       (sum((select extract(epoch from wt.end_time - wt.start_time) / 3600) *
            u.cost_per_hour) * 100) / p.budget as budget_percentage_use,
       u,
       sum((select distinct extract(epoch from wt.end_time - wt.start_time) / 3600) *
           u.cost_per_hour) > p.budget         as budget_exceeded
from projects p
         left join users_projects up on up.projects_id = p.id
         left join working_times wt on p.id = wt.project_id
         left join users u on up.users_id = u.id
group by p.id, p.uuid, p.project_name, p.description, p.start_date, p.end_date, p.budget, u
