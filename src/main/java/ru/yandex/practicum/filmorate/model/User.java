package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.CustomTextAnnotation;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @Email
    private String email;
    @NotNull
    @NotBlank
    @CustomTextAnnotation
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
