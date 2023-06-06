package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException(String.format("Film with id %d not found", id));
        }
        return films.get(id);
    }

    @Override
    public List<Film> getListAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        boolean isNotExist = getListAllFilms().stream()
                .filter(f -> f.getName().equals(film.getName()) &&
                        f.getReleaseDate().equals(film.getReleaseDate())).findFirst().isEmpty();
        if (!isNotExist) {
            throw new FilmAlreadyExistException(String.format("Film %s already exist", film.getName()));
        }
        film.setId(id);
        films.put(id++, film);
        return film;
    }

    @Override
    public Film deleteFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(String.format("Film with id %d not found", film.getId()));
        }
        films.remove(film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(String.format("Film with id %d not found", film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }
}
