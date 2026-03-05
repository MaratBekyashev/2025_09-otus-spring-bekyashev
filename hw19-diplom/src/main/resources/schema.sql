drop table if EXISTS users;
drop table if EXISTS roles;
drop table if EXISTS user_roles;
drop table if EXISTS projects;
drop table if EXISTS project_members;
drop table if EXISTS tasks;
drop table if EXISTS task_comments;
drop table if EXISTS audit_logs;

create table users (
    user_id   bigint generated always as identity,
    user_name varchar(255) not null,
    email     varchar(255),
    password  varchar(255),
    constraint users_pk primary key (user_id)
);

create table roles (
   role_id   bigint generated always as identity,
   role_name varchar(255) not null,
   constraint roles_pk primary key (role_id)
);

create table user_roles (
    user_id bigint not null ,
    role_id bigint not null,
    constraint user_role_uk unique (user_id, role_id)
);

alter table user_roles add constraint user_roles_user_fk foreign key (user_id) references users(user_id);
alter table user_roles add constraint user_roles_role_fk foreign key (role_id) references roles(role_id);

create table projects (
    project_id    bigint generated always as identity,
    name          varchar(255) not null,
    description   varchar2(2000),
    owner_id      bigint,
    create_date   datetime,
    constraint projects_pk primary key (project_id)
);

alter table projects add constraint projects_owner_fk foreign key (owner_id) references users(user_id);

create table project_members (
    id              bigint generated always as identity primary key,
    project_id      bigint not null,
    user_id         bigint not null,
    role_in_project varchar2(255)
);

alter table project_members add constraint project_members_user_fk foreign key (user_id) references users(user_id);
alter table project_members add constraint project_members_project_fk foreign key (project_id) references projects(project_id);

create table tasks (
    task_id      bigint generated always as identity,
    title        varchar(255) not null,
    description  varchar(255),
    status       varchar(255),
    project_id   bigint,
    user_id      bigint,
    create_date  datetime,
    due_date     datetime,
    constraint tasks_pk primary key (task_id)
);

alter table tasks add constraint tasks_project_fk foreign key (project_id) references projects(project_id);
alter table tasks add constraint tasks_user_fk foreign key (user_id) references users(user_id);

create table task_comments (
   comment_id   bigint generated always as identity,
   task_id      bigint not null,
   user_id      bigint not null,
   create_date  datetime,
   constraint task_comments_pk primary key (comment_id)
);

alter table task_comments add constraint task_comments_task_fk foreign key (task_id) references tasks(task_id);
alter table task_comments add constraint task_comments_user_fk foreign key (user_id) references users(user_id);

create table audit_logs (
    id bigint generated always as identity,
    user_id bigint not null,
    action varchar2(2000) not null,
    entity_type varchar2(128),
    entity_id bigint,
    create_date datetime
);

alter table audit_logs add constraint audit_logs_user_fk foreign key (user_id) references users(user_id);


