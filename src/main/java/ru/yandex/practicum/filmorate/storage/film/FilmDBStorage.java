package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            String sql = "select f.film_id, f.film_name, f.film_description, f.film_release_date, f.film_duration, " +
                    "f.film_rating, f.rating_mpa_id, rm.rating_name, fg.genre_id, g.genre_name, l.user_id " +
                    "from films f " +
                    "left join film_genre fg on f.film_id = fg.film_id " +
                    "left join genres g on fg.genre_id = g.genre_id " +
                    "left join ratings_mpa rm on f.rating_mpa_id = rm.rating_id " +
                    "left join likes l on f.film_id = l.film_id " +
                    "WHERE f.film_id = ?";
            List<Film> films = jdbcTemplate.queryForObject(sql, filmRowMapper(), id);
            return films.get(0);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Film id %d not found", id));
        }
    }

    @Override
    public List<Film> getListAllFilms() {
        try {
            String sql = "select f.film_id, f.film_name, f.film_description, f.film_release_date, " +
                    "f.film_duration, f.film_rating, f.rating_mpa_id, rm.rating_name, " +
                    "fg.genre_id, g.genre_name, l.user_id " +
                    "from films f " +
                    "left join film_genre fg on f.film_id = fg.film_id " +
                    "left join ratings_mpa rm on f.rating_mpa_id = rm.rating_id " +
                    "left join genres g on fg.genre_id = g.genre_id " +
                    "left join likes l on f.film_id = l.film_id"
                    ;
            List<Film> films = jdbcTemplate.queryForObject(sql, filmRowMapper());
            films.sort(Comparator.comparing(Film::getId));
            return films;
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into films (film_name, film_description, film_release_date, " +
                "film_duration, film_rating, rating_mpa_id) values (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setLong(5, film.getRate());
            stmt.setLong(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());

        saveGenres(film);

        return film;
    }

    @Override
    public Film deleteFilm(int id) {
        Film film = getFilmById(id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
        return film;
    }

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
        return getFilmById(film.getId());
    }

    @Override
    public void addLike(int userId, int filmId) {
        getFilmById(filmId);
        try {
            jdbcTemplate.queryForObject("select user_id from users where user_id = ?",
                    (rs, rowNum) -> rs.getInt("user_id"), userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("User by id %d not found", userId));
        }
        jdbcTemplate.update("UPDATE films SET film_rating = ? WHERE film_id = ?",
                filmRateById(filmId) + 1, filmId);

        jdbcTemplate.update("insert into likes (user_id, film_id) values (?, ?)", userId, filmId);
    }

    @Override
    public void deleteLike(int userId, int filmId) {
        getFilmById(filmId);
        try {
            jdbcTemplate.queryForObject("select user_id from users where user_id = ?",
                    (rs, rowNum) -> rs.getInt("user_id"), userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("User by id %d not found", userId));
        }
        if (filmRateById(filmId) > 0) {
            jdbcTemplate.update("UPDATE films SET film_rating = ? WHERE film_id = ?",
                    filmRateById(filmId) - 1, filmId);

            jdbcTemplate.update("delete from likes where user_id = ? and film_id = ?", userId, filmId);
        }
    }

    @Override
    public List<Film> topFilms(int count) {
        String sql = "select f.film_id, f.film_name, f.film_description, f.film_release_date, " +
                "f.film_duration, f.film_rating, f.rating_mpa_id, rm.rating_name, " +
                "fg.genre_id, g.genre_name, l.user_id " +
                "from films f " +
                "left join film_genre fg on f.film_id = fg.film_id " +
                "left join genres g on fg.genre_id = g.genre_id " +
                "left join ratings_mpa rm on f.rating_mpa_id = rm.rating_id " +
                "left join likes l on f.film_id = l.film_id " +
                "order by film_rating desc limit ?";
        return jdbcTemplate.queryForObject(sql, filmRowMapper(),count);
    }

    @Override
    public List<Film> topFilmsByYear(int count, int year) {
        String sql = "select f.film_id, f.film_name, f.film_description, f.film_release_date, " +
                "f.film_duration, f.film_rating, f.rating_mpa_id, rm.rating_name, " +
                "fg.genre_id, g.genre_name, l.user_id " +
                "from films f " +
                "left join film_genre fg on f.film_id = fg.film_id " +
                "left join genres g on fg.genre_id = g.genre_id " +
                "left join ratings_mpa rm on f.rating_mpa_id = rm.rating_id " +
                "left join likes l on f.film_id = l.film_id " +
                "where extract(YEAR from f.film_release_date) = ?" +
                "order by film_rating desc";
        List<Film> filmsFromDataBase;
        List<Film> filmsFinal = new ArrayList<>(count);
        try {
            filmsFromDataBase = jdbcTemplate.queryForObject(sql, filmRowMapper(), year);
            for (Film film : filmsFromDataBase) {
                filmsFinal.add(getFilmById(film.getId()));
            }
            return filmsFinal;
        } catch (EmptyResultDataAccessException ignored) {

        }
        return filmsFinal;
    }

    @Override
    public List<Film> topFilmsByGenre(int count, int genreId) {
        String sql = "select f.film_id, f.film_name, f.film_description, f.film_release_date, " +
                "f.film_duration, f.film_rating, f.rating_mpa_id, rm.rating_name, " +
                "fg.genre_id, g.genre_name, l.user_id " +
                "from films f " +
                "left join film_genre fg on f.film_id = fg.film_id " +
                "left join genres g on fg.genre_id = g.genre_id " +
                "left join ratings_mpa rm on f.rating_mpa_id = rm.rating_id " +
                "left join likes l on f.film_id = l.film_id " +
                "where g.genre_id = ?" +
                "order by film_rating desc";

        List<Film> filmsFromDataBase;
        List<Film> filmsFinal = new ArrayList<>(count);
        try {
            filmsFromDataBase = jdbcTemplate.queryForObject(sql, filmRowMapper(), genreId);
            for (Film film : filmsFromDataBase) {
                filmsFinal.add(getFilmById(film.getId()));
            }
            return filmsFinal;
        } catch (EmptyResultDataAccessException ignored) {

        }
        return filmsFinal;
    }

    @Override
    public List<Film> topFilmsByYearAndGenre(int count, int year, int genreId) {
        String sql = "select f.film_id, f.film_name, f.film_description, f.film_release_date, " +
                "f.film_duration, f.film_rating, f.rating_mpa_id, rm.rating_name, " +
                "fg.genre_id, g.genre_name, l.user_id " +
                "from films f " +
                "left join film_genre fg on f.film_id = fg.film_id " +
                "left join genres g on fg.genre_id = g.genre_id " +
                "left join ratings_mpa rm on f.rating_mpa_id = rm.rating_id " +
                "left join likes l on f.film_id = l.film_id " +
                "where g.genre_id = ? and extract(YEAR from f.film_release_date) = ?" +
                "order by film_rating desc";

        List<Film> filmsFromDataBase;
        List<Film> filmsFinal = new ArrayList<>(count);
        try {
            filmsFromDataBase = jdbcTemplate.queryForObject(sql, filmRowMapper(), genreId, year);
            for (Film film : filmsFromDataBase) {
                filmsFinal.add(getFilmById(film.getId()));
            }
            return filmsFinal;
        } catch (EmptyResultDataAccessException ignored) {

        }
        return filmsFinal;
    }

    private RowMapper<List<Film>> filmRowMapper() {
        return (rs, rowNum) -> {
            List<Film> films = new ArrayList<>();
            Film film = new Film(rs.getInt("film_id"),
                    rs.getString("film_name"),
                    rs.getString("film_description"),
                    rs.getDate("film_release_date").toLocalDate(),
                    rs.getInt("film_duration"),
                    new Mpa(rs.getInt("rating_mpa_id"), rs.getString("rating_name")));
            film.setRate(rs.getInt("film_rating"));

            do {
                if (film.getId() == rs.getInt("film_id")) {
                    if (rs.getInt("genre_id") != 0) {
                        film.addGenre(rs);
                        addLikesToRm(film, rs);
                    }
                } else {
                    film = new Film(rs.getInt("film_id"),
                            rs.getString("film_name"),
                            rs.getString("film_description"),
                            rs.getDate("film_release_date").toLocalDate(),
                            rs.getInt("film_duration"),
                            new Mpa(rs.getInt("rating_mpa_id"), rs.getString("rating_name")));
                    film.setRate(rs.getInt("film_rating"));
                    if (rs.getInt("genre_id") != 0) {
                        film.addGenre(rs);
                        addLikesToRm(film, rs);
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

    private Integer filmRateById(int filmId) {
        return jdbcTemplate.queryForObject("select film_rating from films where film_id = ?",
                (rs, rowNum) -> rs.getInt("film_rating"), filmId);
    }

    private void saveGenres(Film film) {
        String sql = "insert into film_genre (film_id, genre_id) values (?, ?)";
        for (Genre genre: film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    private void addLikesToRm(Film film, ResultSet rs) throws SQLException {
        film.getLikes().add(rs.getInt("user_id"));
    }
}
