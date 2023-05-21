package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.CustomTextAnnotation;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @Email
    @NotEmpty
    private String email;
    @NotBlank
    @CustomTextAnnotation
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
}
