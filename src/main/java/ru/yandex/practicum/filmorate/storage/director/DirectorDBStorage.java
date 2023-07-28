package ru.yandex.practicum.filmorate.storage.director;

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
import java.util.Objects;

@Component
public class DirectorDBStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public DirectorDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
    }

    @Override
    public List<Director> getListAllDirectors() {
        String sql = "select * from directors";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeDirector(resultSet));
    }

    @Override
    public Director getDirectorById(int id) {
        String sqlDirector = "SELECT * FROM directors WHERE director_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlDirector, (rs, rowNum) -> makeDirector(rs), id);
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
        return new Director(resultSet.getInt("director_id"), resultSet.getString("director_name"));
    }
}
