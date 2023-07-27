package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> allFilms() {
        log.debug("Film's list: {}", filmService.getListAllFilms());
        return filmService.getListAllFilms();
    }

    @GetMapping ("/director/{directorId}")
    public List<Film> getAllFilmsByDirectorId(@PathVariable int directorId, @RequestParam(value = "sortBy") String sortBy) {
        log.debug("Film's list: {}", filmService.getAllFilmsByDirectorId(directorId,sortBy));
        return filmService.getAllFilmsByDirectorId(directorId,sortBy);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Film added: {}", film);
        return filmService.addFilm(film);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilm(@PathVariable int id) {
        log.debug("Film by id {} was deleted", id);
        return filmService.deleteFilm(id);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Film updated: {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.debug("Film by id: {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Film by id {} has like from user {}", id, userId);
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Film by id {} delete like from user {}", id, userId);
        filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(value = "count", defaultValue = "10", required = false) int count,
                                  @RequestParam(value = "genreId", required = false) Integer genreId,
                                  @RequestParam(value = "year", required = false) Integer year) {
        return filmService.getTopFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(value = "userId") int userId, @RequestParam(value = "friendId") int friendId) {
        log.debug("Common films user id {} and user id {}", userId, friendId);
        return filmService.getCommonFilms(userId,friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam(value = "query") String query, @RequestParam(value = "by") String by) {
        log.debug("Search films by {} and query {}", by, query);
        return filmService.searchFilms(query, by);
    }
}
