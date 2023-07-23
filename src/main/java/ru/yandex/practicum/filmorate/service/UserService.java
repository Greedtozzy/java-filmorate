package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
    }

    public void deleteFriend(int id1, int id2) {
        userStorage.deleteFriend(id1, id2);
    }

    public List<User> listAllFriends(int id) {
        return userStorage.listAllFriends(id);
    }

    public List<User> commonFriends(int id0, int id1) {
        return userStorage.getUserById(id0).getFriendsList().stream()
                .filter(userStorage.getUserById(id1).getFriendsList()::contains).filter(i -> i != 0)
                .map(userStorage::getUserById).distinct().collect(Collectors.toList());
    }
}
