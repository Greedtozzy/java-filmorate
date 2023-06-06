package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Film getFilmById(int id);
    List<Film> getListAllFilms();
    Film addFilm(Film film);
    Film deleteFilm(Film film);
    Film updateFilm(Film film);
}
