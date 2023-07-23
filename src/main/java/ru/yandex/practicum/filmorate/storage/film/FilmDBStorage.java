package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorDBStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorDBStorage directorDBStorage;
    private final String sql = "select f.film_id, f.film_name, f.film_description, f.film_release_date, f.film_duration, " +
            "f.film_rating, f.rating_mpa_id, rm.rating_name, fg.genre_id, g.genre_name, " +
            "l.user_id, fd.director_id, d.director_name " +
            "from films f " +
            "left join film_genre fg on f.film_id = fg.film_id " +
            "left join genres g on fg.genre_id = g.genre_id " +
            "left join ratings_mpa rm on f.rating_mpa_id = rm.rating_id " +
            "left join likes l on f.film_id = l.film_id " +
            "left join film_director fd on f.film_id = fd.film_id " +
            "left join directors d on fd.director_id = d.director_id ";

    @Override
    public Film getFilmById(int id) {
        try {
            List<Film> films = jdbcTemplate.queryForObject(sql + "WHERE f.film_id = ?", filmRowMapper(), id);
            return films.get(0);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(String.format("Film id %d not found", id));
        }
    }

    @Override
    public List<Film> getListAllFilms() {
        try {
            List<Film> films = jdbcTemplate.queryForObject(sql, filmRowMapper());
            films.sort(Comparator.comparing(Film::getId));
            return films;
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Film> getAllFilmsByDirectorId(int directorId, String sortBy) {
        directorDBStorage.getDirectorById(directorId);
        try {
            switch (sortBy) {
                case "year":
                    return jdbcTemplate.queryForObject(sql +
                            "where fd.director_id = ? order by f.film_release_date",
                            filmRowMapper(), directorId);
                case "likes":
                    return jdbcTemplate.queryForObject(sql +
                            "where fd.director_id = ? order by f.film_rating desc",
                            filmRowMapper(), directorId);
                default:
                    throw new RuntimeException();
            }
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
        saveDirectors(film);

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
        addFilmGenres(film);

        jdbcTemplate.update("DELETE FROM film_director WHERE film_id = ?", film.getId());
        addFilmDirectors(film);
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
        jdbcTemplate.update("insert into likes (user_id, film_id) values (?, ?)", userId, filmId);
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
            throw new UserNotFoundException(String.format("User by id %d not found", userId));
        }
        jdbcTemplate.update("delete from likes where user_id = ? and film_id = ?", userId, filmId);
        if (filmRateById(filmId) > 0) {
            jdbcTemplate.update("UPDATE films SET film_rating = ? WHERE film_id = ?",
                    filmRateById(filmId) - 1, filmId);
        }
    }

    @Override
    public List<Film> topFilms(int count) {
        return jdbcTemplate.queryForObject(sql + "order by film_rating desc limit ?", filmRowMapper(),count);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        List<Film> films = getListAllFilms();

        switch (by) {
            case "title":
                return films.stream()
                        .filter(film -> film.getName().toLowerCase().contains(query.toLowerCase()))
                        .sorted(Comparator.comparing(Film::getRate))
                        .collect(Collectors.toList());
            case "director":
                return films.stream()
                        .filter(film -> {
                            boolean isContains = false;
                            for (Director director : film.getDirectors()) {
                                if (director.getName().toLowerCase().contains(query.toLowerCase())) {
                                    isContains = true;
                                    break;
                                }
                            }
                            return isContains;
                        })
                        .sorted(Comparator.comparing(Film::getRate))
                        .collect(Collectors.toList());
            case "title,director":
            case "director,title":
                return films.stream()
                        .filter(film -> {
                            if (film.getName().toLowerCase().contains(query.toLowerCase())) {
                                return true;
                            }
                            boolean isContains = false;
                            for (Director director : film.getDirectors()) {
                                if (director.getName().toLowerCase().contains(query.toLowerCase())) {
                                    isContains = true;
                                    break;
                                }
                            }
                            return isContains;
                        })
                        .sorted(Comparator.comparing(Film::getRate))
                        .collect(Collectors.toList());
            default: return new ArrayList<>();
        }
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
                    }
                    if (rs.getInt("director_id") != 0) {
                        film.addDirector(rs);
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
                    }
                    if (rs.getInt("director_id") != 0) {
                        film.addDirector(rs);
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

    private void saveDirectors(Film film) {
        String sql = "insert into film_director (film_id, director_id) values (?, ?)";
        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(sql, film.getId(), director.getId());
        }
    }

    private void addFilmGenres(Film film) {
        jdbcTemplate.batchUpdate("insert into film_genre (film_id, genre_id) values (?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, film.getId());
                preparedStatement.setInt(2, new ArrayList<>(film.getGenres()).get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return film.getGenres().size();
            }
        });
    }

    private void addFilmDirectors(Film film) {
        jdbcTemplate.batchUpdate("insert into film_director (film_id, director_id) values (?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, film.getId());
                preparedStatement.setInt(2, new ArrayList<>(film.getDirectors()).get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return film.getDirectors().size();
            }
        });
    }
}
