create table if not exists authors (
    id bigint primary key auto_increment,
    full_name varchar(255)
);

create table if not exists genres (
    id bigint primary key auto_increment,
    name varchar(255)
);

create table if not exists books (
    id bigint primary key auto_increment,
    title varchar(255),
    author_id bigint references authors (id) on delete cascade,
    genre_id bigint references genres(id) on delete cascade
);

create table if not exists comments (
     id bigint primary key auto_increment,
     content varchar(255),
     book_id bigint references books (id) on delete cascade
);