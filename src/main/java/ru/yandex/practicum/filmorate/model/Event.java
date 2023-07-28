package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @NotNull
    int eventId;
    @NotNull
    int entityId;
    @NotNull
    EventType eventType;
    @NotNull
    EventOperation operation;
    @NotNull
    int userId;
    @NotNull
    long timestamp;
}
