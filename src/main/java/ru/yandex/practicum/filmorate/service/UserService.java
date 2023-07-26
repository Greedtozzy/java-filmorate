package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public List<User> getListAllUsers() {
        return userStorage.getListAllUsers();
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User deleteUser(int id) {
        return userStorage.deleteUser(id);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(int id1, int id2) {
        userStorage.addFriend(id1, id2);
        eventStorage.addEvent(id2, "FRIEND", "ADD", id1);
    }

    public void deleteFriend(int id1, int id2) {
        userStorage.deleteFriend(id1, id2);
        eventStorage.addEvent(id2, "FRIEND", "REMOVE", id1);
    }

    public List<User> listAllFriends(int id) {
        return userStorage.listAllFriends(id);
    }

    public List<User> commonFriends(int id0, int id1) {
        return userStorage.getUserById(id0).getFriendsList().stream()
                .filter(userStorage.getUserById(id1).getFriendsList()::contains).filter(i -> i != 0)
                .map(userStorage::getUserById).distinct().collect(Collectors.toList());
    }

    public List<Film> getRecommendations(@PathVariable int id) {
        return userStorage.getRecommendations(id);
    }
}
