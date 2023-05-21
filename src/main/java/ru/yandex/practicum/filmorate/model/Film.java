package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.CustomDateAnnotation;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotNull
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
}
