package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("filmDBStorage")
@AllArgsConstructor

public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public Film getFilmById(int id) {
        try {
            List<Film> films = jdbcTemplate.queryForObject("select f.film_id, f.film_name, f.film_description, " +
                            "f.film_release_date, f.film_duration, f.film_rating, f.rating_mpa_id, fg.genre_id " +
                            "from films f " +
                            "left join film_genre fg on f.film_id = fg.film_id " +
                            "WHERE f.film_id = ?",
                    filmRowMapper(), id);
            return films.get(0);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Film id {} not found", id));
        }
    }

    @Override
    public List<Film> getListAllFilms() {
        try {
            List<Film> films = jdbcTemplate.queryForObject("select f.film_id, f.film_name, f.film_description, " +
                            "f.film_release_date, f.film_duration, f.film_rating, f.rating_mpa_id, fg.genre_id " +
                            "from films f " +
                            "left join film_genre fg on f.film_id = fg.film_id",
                    filmRowMapper());
            films.sort(Comparator.comparing(Film::getId));
            return films;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> params = Map.of("film_name", film.getName(),
                "film_description", film.getDescription(),
                "film_release_date", film.getReleaseDate(),
                "film_duration", film.getDuration(),
                "film_rating", film.getRate());

        film.setId(simpleJdbcInsert.executeAndReturnKey(params).intValue());

        if (film.getMpa().getId() > 0 && film.getMpa().getId() < 6) {
            jdbcTemplate.update("UPDATE films SET rating_mpa_id = ? WHERE film_id = ?",
                    film.getMpa().getId(), film.getId());
        }

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genre.getId());
        }
        return film;
    }

    @Override
    public Film deleteFilm(int id) {
        Film film = getFilmById(id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
        return film;
    }

    /** По моему мнению, список лайков не должен изменяться при обновлении информации о фильме.
     * Если требуется, исправлю.*/
    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        jdbcTemplate.update("UPDATE films " +
                "SET film_name = ?, " +
                "film_description = ?, " +
                "film_release_date = ?, " +
                "film_duration = ?, " +
                "film_rating = ? " +
                "WHERE film_id = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getId());

        if (film.getMpa().getId() > 0 && film.getMpa().getId() < 6) {
            jdbcTemplate.update("UPDATE films SET rating_mpa_id = ? WHERE film_id = ?",
                    film.getMpa().getId(), film.getId());
        }

        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genre.getId());
        }
        return film;
    }

    @Override
    public void addLike(int userId, int filmId) {
        getFilmById(filmId);
        try {
            jdbcTemplate.queryForObject("select user_id from users where user_id = ?",
                    (rs, rowNum) -> rs.getInt("user_id"), userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("User by id {} not found", userId));
        }
        jdbcTemplate.update("UPDATE films SET film_rating = ? WHERE film_id = ?",
                filmRateById(filmId) + 1, filmId);
    }

    @Override
    public void deleteLike(int userId, int filmId) {
        getFilmById(filmId);
        try {
            jdbcTemplate.queryForObject("select user_id from users where user_id = ?",
                    (rs, rowNum) -> rs.getInt("user_id"), userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("User by id {} not found", userId));
        }
        if (filmRateById(filmId) > 0) {
            jdbcTemplate.update("UPDATE films SET film_rating = ? WHERE film_id = ?",
                    filmRateById(filmId) - 1, filmId);
        }
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM genres", genreRowMapper());
        genres.sort(Comparator.comparing(Genre::getId));
        return genres;
    }

    @Override
    public Genre getGenreById(int id) {
        if (id < 1 || id > 6) {
            throw new FilmNotFoundException("Genre id mast be from 1 to 6");
        }
        return jdbcTemplate.queryForObject("SELECT genre_id FROM genres WHERE genre_id = ?",
                genreRowMapper(), id);
    }

    @Override
    public List<Mpa> getRatings() {
        List<Mpa> mpas = jdbcTemplate.query("SELECT * FROM ratings_mpa",
                mpaRatingRowMapper());
        mpas.sort(Comparator.comparing(Mpa::getId));
        return mpas;
    }

    @Override
    public Mpa getRatingById(int id) {
        if (id < 1 || id > 5) {
            throw new FilmNotFoundException("MPA rating may be from 1 to 5");
        }
        return jdbcTemplate.queryForObject("SELECT * FROM ratings_mpa WHERE rating_id = ?",
                mpaRatingRowMapper(), id);
    }

    @Override
    public List<Film> topFilms(int count) {
        return jdbcTemplate.queryForObject("select f.film_id, f.film_name, f.film_description, " +
                        "f.film_release_date, f.film_duration, f.film_rating, f.rating_mpa_id, fg.genre_id " +
                        "from films f " +
                        "left join film_genre fg on f.film_id = fg.film_id " +
                        "order by film_rating desc limit ?",
                filmRowMapper(),count);
    }

    private RowMapper<List<Film>> filmRowMapper() {
        return (rs, rowNum) -> {
            List<Film> films = new ArrayList<>();
            Film film = new Film(rs.getInt("film_id"),
                    rs.getString("film_name"),
                    rs.getString("film_description"),
                    rs.getDate("film_release_date").toLocalDate(),
                    rs.getInt("film_duration"));
            film.setRate(rs.getInt("film_rating"));
            film.getMpa().setId(rs.getInt("rating_mpa_id"));

            do {
                if (film.getId() == rs.getInt("film_id")) {
                    if (rs.getInt("genre_id") != 0) {
                        Genre genre = new Genre();
                        genre.setId(rs.getInt("genre_id"));
                        film.getGenres().add(genre);
                    }
                } else {
                    film = new Film(rs.getInt("film_id"),
                            rs.getString("film_name"),
                            rs.getString("film_description"),
                            rs.getDate("film_release_date").toLocalDate(),
                            rs.getInt("film_duration"));
                    film.setRate(rs.getInt("film_rating"));
                    film.getMpa().setId(rs.getInt("rating_mpa_id"));
                    if (rs.getInt("genre_id") != 0) {
                        Genre genre = new Genre();
                        genre.setId(rs.getInt("genre_id"));
                        film.getGenres().add(genre);
                    }
                }
                if (!films.stream()
                        .map(Film::getId)
                        .collect(Collectors.toList()).contains(film.getId())) {
                    films.add(film);
                }
            } while (rs.next());
            return films;
        };
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            return genre;
        };
    }

    private RowMapper<Mpa> mpaRatingRowMapper() {
        return ((rs, rowNum) -> {
            Mpa rating = new Mpa();
            rating.setId(rs.getInt("rating_id"));
            return rating;
        });
    }

    private Integer filmRateById(int filmId) {
        return jdbcTemplate.queryForObject("select film_rating from films where film_id = ?",
                (rs, rowNum) -> rs.getInt("film_rating"), filmId);
    }
}
