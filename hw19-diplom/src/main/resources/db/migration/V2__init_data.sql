-- пароль - qwerty
insert into users(login, user_name, password, email)
values ('admin', 'Администратор', '$2a$10$bvty4GBnm1mdky5pkNa9v.gOw6l20d1BNpIBNwEQ.XGJ96a8STymG', 'admin@mail.ru'),
       ('user', 'Пользователь 1', '$2a$10$bvty4GBnm1mdky5pkNa9v.gOw6l20d1BNpIBNwEQ.XGJ96a8STymG', 'user@mail.ru'),
       ('user2', 'Пользователь 2', '$2a$10$bvty4GBnm1mdky5pkNa9v.gOw6l20d1BNpIBNwEQ.XGJ96a8STymG', 'user2@mail.ru');

insert into roles(role_name)
values ('ROLE_ADMIN'), ('ROLE_USER'), ('ROLE_MANAGER');


insert into user_roles (user_id, role_id)
values (1, 1), (2, 2);