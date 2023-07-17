package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getGenres() {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM genres", genreRowMapper());
        genres.sort(Comparator.comparing(Genre::getId));
        return genres;
    }

    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genres WHERE genre_id = ?",
                    genreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(String.format("Genre by id %d not found", id));
        }
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }
}
