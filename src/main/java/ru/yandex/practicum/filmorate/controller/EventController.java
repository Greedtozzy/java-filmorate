package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.EventService;

import java.util.List;

@RestController
@RequestMapping
@Slf4j
@AllArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/users/{id}/feed")
    public List<Event> getAllEvents(@PathVariable int id) {
        log.debug("Event list output");
        return eventService.getAllEvents(id);
    }
}
