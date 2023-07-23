package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
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

    public List<Film> getAllFilmsByDirectorId(int directorId, String sortBy) {
        return filmStorage.getAllFilmsByDirectorId(directorId,sortBy);
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
        eventStorage.addEvent(filmId, "LIKE", "ADD", userId);
    }

    public void deleteLike(int userId, int filmId) {
        filmStorage.deleteLike(userId, filmId);
        eventStorage.addEvent(filmId, "LIKE", "REMOVE", userId);
    }

    public List<Film> topFilms(int count) {
        return filmStorage.topFilms(count);
    }
}
