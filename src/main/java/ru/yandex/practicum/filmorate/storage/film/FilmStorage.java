package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilmById(int id);

    List<Film> getListAllFilms();

    Film addFilm(Film film);

    Film deleteFilm(int id);

    Film updateFilm(Film film);

    void addLike(int userId, int filmId);

    void deleteLike(int userId, int filmId);

    List<Film> topFilms(int count);
}