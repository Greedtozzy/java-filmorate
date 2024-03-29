package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SortBy;

import java.util.List;

public interface FilmStorage {

    Film getFilmById(int id);

    List<Film> getListAllFilms();

    List<Film> getAllFilmsByDirectorId(int directorId, SortBy sortBy);

    Film addFilm(Film film);

    Film deleteFilm(int id);

    Film updateFilm(Film film);

    void addLike(int userId, int filmId);

    void deleteLike(int userId, int filmId);

    List<Film> topFilms(int count);

    List<Film> searchFilms(String query, String by);

    List<Film> topFilmsByYear(int count, int year);

    List<Film> topFilmsByGenre(int count, int genreId);

    List<Film> topFilmsByYearAndGenre(int count, int year, int genreId);

    List<Film> getCommonFilms(int userId, int friendId);
}