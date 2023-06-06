package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(String.format("User with id %d not found", id));
        }
        return users.get(id);
    }

    @Override
    public List<User> getListAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        boolean isNotExist = getListAllUsers().stream()
                        .filter(u -> u.getEmail().equals(user.getEmail())).findFirst().isEmpty();
        if (!isNotExist) {
            throw new UserAlreadyExistException(String.format("User with %s email is already exist", user.getEmail()));
        }
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(id++, user);
        return user;
    }

    @Override
    public User deleteUser(int userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(String.format("User with id %d not found", userId));
        }
        return users.remove(userId);
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(String.format("User with id %d not found", user.getId()));
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }
}
