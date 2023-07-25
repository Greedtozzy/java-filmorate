drop table if exists users, ratings_mpa, films, genres, film_genre, friendships, reviews,
directors, film_director, event_types, event_operations, events, likes, review_like;


create table if not exists users (
user_id serial not null primary key,
user_name varchar(100) not null,
user_login varchar(100) not null unique,
user_email varchar(255) not null unique,
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

create table if not exists reviews (
review_id serial not null primary key,
review_content varchar(255) not null,
review_is_positive boolean not null,
user_id int references users(user_id) on delete cascade,
film_id int references films(film_id) on delete cascade,
review_useful int not null
);

create table if not exists directors (
director_id serial not null primary key,
director_name varchar(255) not null unique
);

create table if not exists film_director (
film_id int not null references films(film_id) on delete cascade,
director_id int not null references directors(director_id) on delete cascade,
primary key (film_id, director_id)
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

create table if not exists friendships (
user1_id int not null references users(user_id) on delete cascade,
user2_id int not null references users(user_id) on delete cascade,
primary key (user1_id, user2_id)
);

create table if not exists event_types (
event_type_id serial not null primary key,
event_type_name varchar(7) not null unique
);

create table if not exists event_operations (
event_operation_id serial not null primary key,
event_operation_name varchar(7) not null unique
);

create table if not exists events (
event_id serial not null primary key,
user_id int not null references users(user_id) on delete cascade,
event_type_id int not null references event_types(event_type_id) on delete cascade,
event_operation_id int not null references event_operations(event_operation_id) on delete cascade,
entity_id int not null,
event_timestamp bigint not null
);

create table if not exists likes (
user_id int not null references users(user_id) on delete cascade,
film_id int not null references films(film_id) on delete cascade,
primary key (user_id, film_id)
);

create table if not exists review_like (
like_user_id int not null references users(user_id) on delete cascade,
review_id int not null references reviews(review_id) on delete cascade,
is_like boolean not null,
primary key (like_user_id, review_id)
);