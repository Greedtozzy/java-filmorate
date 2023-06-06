package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    UserStorage userStorage;
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

    public void addFriend(int id0, int id1) {
        User u0 = userStorage.getUserById(id0);
        User u1 = userStorage.getUserById(id1);
        u0.friendsList.add(id1);
        u1.friendsList.add(id0);
    }

    public void deleteFriend(int id0, int id1) {
        userStorage.getUserById(id0).friendsList.remove(id1);
        userStorage.getUserById(id1).friendsList.remove(id0);
    }

    public List<User> listAllFriends(int id) {
        List<User> friends = new ArrayList<>();
        friends = userStorage.getUserById(id).friendsList.stream()
                .map(i -> userStorage.getUserById(i))
                .collect(Collectors.toList());
        return friends;
    }

    public List<User> commonFriends(int id0, int id1) {
        return userStorage.getUserById(id0).friendsList.stream()
                .filter(userStorage.getUserById(id1).friendsList::contains)
                .map(i -> userStorage.getUserById(i)).distinct().collect(Collectors.toList());
    }
}
