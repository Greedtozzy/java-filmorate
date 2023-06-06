package ru.yandex.practicum.filmorate.model;

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
    public final Set<Integer> likes = new HashSet<>();
}
