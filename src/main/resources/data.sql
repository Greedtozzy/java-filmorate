merge into event_operations (event_operation_id, event_operation_name) values (1, 'REMOVE'), (2, 'ADD'), (3, 'UPDATE');
merge into event_types (event_type_id, event_type_name) values (1, 'LIKE'), (2, 'REVIEW'), (3, 'FRIEND');
merge into genres key (genre_id) values (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');
merge into ratings_mpa key (rating_id) values (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');