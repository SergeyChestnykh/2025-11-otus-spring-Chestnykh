create table authors (
                         id BIGSERIAL primary key,
                         full_name varchar(255) not null
);

create table genres (
                        id BIGSERIAL primary key,
                        name varchar(255) not null
);

create table books (
                       id BIGSERIAL primary key,
                       title varchar(255) not null,
                       author_id bigint references authors(id)
);

create table books_genres (
                              book_id bigint references books(id),
                              genre_id bigint references genres(id)
);

create table comments (
                          id BIGSERIAL primary key,
                          text varchar(255),
                          book_id bigint references books(id)
);