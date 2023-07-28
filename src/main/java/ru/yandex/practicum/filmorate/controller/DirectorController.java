package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
@AllArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> getListAllDirectors() {
        log.debug("Director's list: {}", directorService.getListAllDirectors());
        return directorService.getListAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.debug("Director by id: {}", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.debug("Director added: {}", director);
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.debug("Director updated: {}", director);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public Director deleteDirector(@PathVariable int id) {
        log.debug("Director by id {} was deleted", id);
        return directorService.deleteDirector(id);
    }
}
