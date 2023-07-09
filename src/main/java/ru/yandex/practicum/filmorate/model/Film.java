package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.CustomDateAnnotation;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @Past
    @CustomDateAnnotation
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private long duration;
    private int rate;
    private final Mpa mpa;
    private final Set<Genre> genres = new HashSet<>();
    @JsonIgnore
    private final transient Set<Integer> likes = new HashSet<>();

    public Film(int id, String name, String description, LocalDate releaseDate, long duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.rate = 0;
    }
}
