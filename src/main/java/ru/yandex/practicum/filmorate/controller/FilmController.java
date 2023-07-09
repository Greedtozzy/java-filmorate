package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> allFilms() {
        log.debug("Film's list: {}", filmService.getListAllFilms());
        return filmService.getListAllFilms();
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Film added: {}", film);
        return filmService.addFilm(film);
    }

    @DeleteMapping("/films/{id}")
    public Film deleteFilm(@PathVariable int id) {
        log.debug("Film by id {} was deleted", id);
        return filmService.deleteFilm(id);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Film updated: {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.debug("Film by id: {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Film by id {} has like from user {}", id, userId);
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("Film by id {} delete like from user {}", id, userId);
        filmService.deleteLike(userId, id);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(@RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        log.debug("Top {} films", count);
        return filmService.topFilms(count);
    }




}
