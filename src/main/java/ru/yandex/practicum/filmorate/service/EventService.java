package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class EventService {
    private final EventStorage eventStorage;
    private final UserDBStorage userStorage;

    public EventService(@Qualifier("eventStorage") EventStorage eventStorage, UserDBStorage userStorage) {
        this.eventStorage = eventStorage;
        this.userStorage = userStorage;
    }

    public List<Event> getAllEvents(int id) {
        userStorage.getUserById(id); // проверка существования пользователя
        return eventStorage.getAllEvents(id);
    }
}
