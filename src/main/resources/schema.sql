drop table if exists users, ratings_mpa, films, genres, film_genre, friendships_statuses, friendships;

create table if not exists users (
user_id serial not null primary key,
user_name varchar(100) not null,
user_login varchar(100) not null,
user_email varchar(255) not null,
user_birthday date
);

create table if not exists ratings_mpa (
rating_id serial not null primary key,
rating_name varchar(10) not null unique
);

create table if not exists films (
film_id serial not null primary key,
film_name varchar(100) not null,
film_description varchar(200) not null,
film_release_date date not null,
film_duration int not null,
film_rating int default (0),
rating_mpa_id int references ratings_mpa(rating_id)
);

create table if not exists genres (
genre_id serial not null primary key,
genre_name varchar(20) not null unique
);

create table if not exists film_genre (
film_id int not null references films(film_id) on delete cascade,
genre_id int not null references genres(genre_id) on delete cascade,
primary key (film_id, genre_id)
);

create table if not exists friendships_statuses (
friendships_status_id serial not null primary key,
friendships_status_name varchar(20) not null unique
);

create table if not exists friendships (
user1_id int not null references users(user_id),
user2_id int not null references users(user_id),
friendships_status_id int not null references friendships_statuses(friendships_status_id),
primary key (user1_id, user2_id)
);