package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    @NotNull
    int reviewId;
    @NotBlank
    @NotNull
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    final Integer userId;
    @NotNull
    final Integer filmId;
    int useful;
    @JsonIgnore
    final transient Map<Integer, Boolean> likes = new HashMap<>();


    public void setIsPositive(boolean isPositive) {
        this.isPositive = isPositive;
    }

    public boolean getIsPositive() {
        return isPositive;
    }
}
