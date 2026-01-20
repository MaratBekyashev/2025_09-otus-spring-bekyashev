insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3');

insert into books(title, author_id, genre_id)
values ('BookTitle_1', 1, 1), ('BookTitle_2', 2, 2), ('BookTitle_3', 3, 3);

insert into comments(content, book_id)
values ('Comment_1', 1), ('Comment_2', 1), ('Comment_3', 1),
       ('Comment_4', 2), ('Comment_5', 2), ('Comment_6', 2);

-- Пароль : qwerty
insert into users (user_id, user_name, password) values (1, 'admin', '$2a$10$dlfS8LwHCW5Y.X82LzitLupJTZXEOLkX6wVfO4Gkpmd2I1/WliypO');
-- Пароль : password
insert into users (user_id, user_name, password) values (2, 'reader', '$2a$10$Fu1rbBg5NSCojVWYOtsOaOPH6h4I3pvgMPoeh/wR1EHrhOw9OX7N.');
-- Пароль : password
insert into users (user_id, user_name, password) values (3, 'guest', '$2a$10$Fu1rbBg5NSCojVWYOtsOaOPH6h4I3pvgMPoeh/wR1EHrhOw9OX7N.');

insert into roles (role_id, role_name) values (1, 'ADMIN');
insert into roles (role_id, role_name) values (2, 'READER');
insert into roles (role_id, role_name) values (3, 'GUEST');

insert into user_role (user_id, role_id) values (1,1);
insert into user_role (user_id, role_id) values (1,2);
insert into user_role (user_id, role_id) values (2,2);