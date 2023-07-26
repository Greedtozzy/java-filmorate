package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    private final UserDBStorage userStorage;

    public List<Event> getAllEvents(int id) {
        userStorage.getUserById(id);
        return eventStorage.getAllEvents(id);
    }
}
