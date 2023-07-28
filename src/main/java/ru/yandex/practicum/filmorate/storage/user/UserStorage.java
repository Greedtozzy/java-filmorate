package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {

    User getUserById(int id);

    List<User> getListAllUsers();

    User addUser(User user);

    User deleteUser(int id);

    User updateUser(User user);

    void addFriend(int id1, int id2);

    void deleteFriend(int id0, int id1);

    List<User> listAllFriends(int id);

    List<Film> getRecommendations(int id);
}
