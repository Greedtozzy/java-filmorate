package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryUserStorage")
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

    @Override
    public void addFriend(int id1, int id2) {
        User u0 = getUserById(id1);
        User u1 = getUserById(id2);
        u0.getFriendsList().add(id2);
        u1.getFriendsList().add(id1);
    }

    @Override
    public void deleteFriend(int id0, int id1) {
        getUserById(id0).getFriendsList().remove(id1);
        getUserById(id1).getFriendsList().remove(id0);
    }

    @Override
    public List<User> listAllFriends(int id) {
        return getUserById(id).getFriendsList().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}
