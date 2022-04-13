--создать таблицы
create table iql_user.department (
    id uuid not null primary key,
    name text not null
);

alter table iql_user.users
    add column department_id uuid references iql_user.department (id);

-- миграция данных
insert into iql_user.department (id, name)
    values ('c35b9fff-8f4c-460b-a87d-25123b8ef368', 'Отдел разработки ПО');

UPDATE iql_user.users SET department_id = 'c35b9fff-8f4c-460b-a87d-25123b8ef368'
  WHERE id IN (select id from iql_user.users);

-- добавить ограничения на столбцы
alter table iql_user.users
    alter column department_id set not null;