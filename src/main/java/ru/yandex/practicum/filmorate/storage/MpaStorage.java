package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getRatings() {
        List<Mpa> mpas = jdbcTemplate.query("SELECT * FROM ratings_mpa",
                mpaRatingRowMapper());
        mpas.sort(Comparator.comparing(Mpa::getId));
        return mpas;
    }

    public Mpa getRatingById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM ratings_mpa WHERE rating_id = ?",
                    mpaRatingRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new MpaNotFoundException(String.format("Mpa by id %d not found", id));
        }
    }

    private RowMapper<Mpa> mpaRatingRowMapper() {
        return ((rs, rowNum) -> new Mpa(rs.getInt("rating_id"), rs.getString("rating_name")));
    }
}
