package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class DirectorDBStorage implements DirectorStorage {
    private final Logger log = LoggerFactory.getLogger(DirectorDBStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    @Override
    public List<Director> getListAllDirectors() {
        String sql = "select * from directors";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeDirector(resultSet));
    }

    @Override
    public Director getDirectorById(int id) {
        String sqlDirector = "SELECT * FROM directors WHERE director_id = ?";
        try {
            Director director = jdbcTemplate.queryForObject(sqlDirector, (rs, rowNum) -> makeDirector(rs), id);
            log.info("Найден режиссёр: {} {}", director.getId(), director.getName());
            return director;
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException("Режиссёр с идентификатором " +
                    id + " не зарегистрирован!");
        }
    }

    @Override
    public Director addDirector(Director director) {
        Map<String, String> params = Map.of(
                "director_name", director.getName());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        director.setId(id.intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        getDirectorById(director.getId());
        String sqlDirector = "UPDATE directors set director_name = ? WHERE director_id = ?";
        jdbcTemplate.update(sqlDirector, director.getName(), director.getId());
        return director;

    }

    @Override
    public Director deleteDirector(int id) {
        Director director = getDirectorById(id);
        String sqlDirector = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sqlDirector, id);
        return director;
    }

    private Director makeDirector(ResultSet resultSet) throws SQLException {
        int directorId = resultSet.getInt("director_id");
        return new Director(directorId, resultSet.getString("director_name"));
    }
}
