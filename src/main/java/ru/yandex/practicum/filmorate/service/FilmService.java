package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SortBy;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    public List<Film> getListAllFilms() {
        return filmStorage.getListAllFilms();
    }

    public List<Film> getAllFilmsByDirectorId(int directorId, SortBy sortBy) {
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
        eventStorage.addEvent(filmId, EventType.LIKE, EventOperation.ADD, userId);
    }

    public void deleteLike(int userId, int filmId) {
        filmStorage.deleteLike(userId, filmId);
        eventStorage.addEvent(filmId, EventType.LIKE, EventOperation.REMOVE, userId);
    }

    public List<Film> getTopFilms(int count, Integer genreId, Integer year) {
        if (genreId == null && year == null) {
            return topFilms(count);
        }
        if (genreId == null) {
            return topFilmsByYear(count, year);
        }
        if (year == null) {
            return topFilmsByGenre(count, genreId);
        }
        return topFilmsByYearAndGenre(count, year, genreId);
    }

    private List<Film> topFilms(int count) {
        return filmStorage.topFilms(count);
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }

    private List<Film> topFilmsByYear(int count, int year) {
        return filmStorage.topFilmsByYear(count, year);
    }

    private List<Film> topFilmsByGenre(int count, int genreId) {
        return filmStorage.topFilmsByGenre(count, genreId);
    }

    private List<Film> topFilmsByYearAndGenre(int count, int year, int genreId) {
        return filmStorage.topFilmsByYearAndGenre(count, year, genreId);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}
