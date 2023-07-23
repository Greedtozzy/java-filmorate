package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getListAllFilms() {
        return filmStorage.getListAllFilms();
    }

    public List<Film> getAllFilmsByDirectorId(int directorId, String sortBy) {
        return filmStorage.getAllFilmsByDirectorId(directorId, sortBy);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film deleteFilm(int id) {
        return filmStorage.deleteFilm(id);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(int userId, int filmId) {
        filmStorage.addLike(userId, filmId);
    }

    public void deleteLike(int userId, int filmId) {
        filmStorage.deleteLike(userId, filmId);
    }

    public List<Film> topFilms(int count) {
        return filmStorage.topFilms(count);
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }

    public List<Film> topFilmsByYear(int count, int year) {
        return filmStorage.topFilmsByYear(count, year);
    }

    public List<Film> topFilmsByGenre(int count, int genreId) {
        return filmStorage.topFilmsByGenre(count, genreId);
    }

    public List<Film> topFilmsByYearAndGenre(int count, int year, int genreId) {
        return filmStorage.topFilmsByYearAndGenre(count, year, genreId);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}
