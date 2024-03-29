package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validators.CustomDateAnnotation;

import javax.validation.constraints.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    int id;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @NotNull
    @CustomDateAnnotation
    LocalDate releaseDate;
    @NotNull
    @Positive
    long duration;
    int rate;
    final Mpa mpa;
    final Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
    final Set<Director> directors = new TreeSet<>(Comparator.comparing(Director::getId));
    @JsonIgnore
    final transient Set<Integer> likes = new HashSet<>();

    public Film(int id, String name, String description, LocalDate releaseDate, long duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.rate = 0;
    }

    public void addGenre(ResultSet rs) throws SQLException {
        genres.add(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
    }

    public void addDirector(ResultSet rs) throws SQLException {
        directors.add(new Director(rs.getInt("director_id"), rs.getString("director_name")));
    }
}
