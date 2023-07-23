package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilmById(int id);

    List<Film> getListAllFilms();

    List<Film> getAllFilmsByDirectorId(int directorId, String sortBy);

    Film addFilm(Film film);

    Film deleteFilm(int id);

    Film updateFilm(Film film);

    void addLike(int userId, int filmId);

    void deleteLike(int userId, int filmId);

    List<Film> topFilms(int count);

    public List<Film> topFilmsByYear(int count, int year);

    public List<Film> topFilmsByGenre(int count, int genreId);

    public List<Film> topFilmsByYearAndGenre(int count, int year, int genreId);
}