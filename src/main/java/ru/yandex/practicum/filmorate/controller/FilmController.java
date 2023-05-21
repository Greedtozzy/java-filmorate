package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<Film> allFilms() {
        List<Film> listFilms = new ArrayList<>(films.values());
        log.debug("Film's list: {}", listFilms);
        return listFilms;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        film.setId(id);
        films.put(id++, film);
        log.debug("Film added: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("No film with this ID");
        }

        films.put(film.getId(), film);
        log.debug("Film updated: {}", film);
        return film;
    }
}
