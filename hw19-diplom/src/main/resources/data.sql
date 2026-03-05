insert into users(user_name, password, email)
values ('admin', 'qwerty', 'admin@mail.ru'),
       ('user', 'qwerty', 'user@mail.ru'),
       ('user2', 'qwerty', 'user2@mail.ru');

insert into roles(role_name)
values ('ROLE_ADMIN'), ('ROLE_USER'), ('ROLE_MANAGER');


insert into user_roles (user_id, role_id)
values (1, 1), (2, 2);