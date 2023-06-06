package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilmById(int id);

    List<Film> getListAllFilms();

    Film addFilm(Film film);

    Film deleteFilm(Film film);

    Film updateFilm(Film film);
}