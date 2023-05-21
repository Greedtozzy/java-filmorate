package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int id = 1;
    public final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> allUsers() {
        List<User> listUsers = new ArrayList<>(users.values());
        log.debug("User's list: {}", listUsers);
        return listUsers;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(id);
        users.put(id++, user);
        log.debug("User added: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("No user with this ID");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("User updated: {}", user);
        return user;
    }
}
