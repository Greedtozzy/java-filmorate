package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User getUserById(int id);
    List<User> getListAllUsers();
    User addUser(User user);
    User deleteUser(int id);
    User updateUser(User user);
}
