package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validators.CustomTextAnnotation;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    int id;
    @Email
    @NotEmpty
    String email;
    @NotBlank
    @CustomTextAnnotation
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;
    @JsonIgnore
    final transient Set<Integer> friendsList = new HashSet<>();
}
