package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserDBStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(int id) {
        try {
            List<User> users = jdbcTemplate.queryForObject("SELECT * " +
                    "FROM users u " +
                    "LEFT JOIN friendships f ON u.user_id = f.user1_id " +
                    "WHERE u.user_id = ?", userRowMapper(), id);
            if (users != null) {
                return users.get(0);
            } else {
                throw new UserNotFoundException(String.format(String.format("User id %d not found", id)));
            }
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format(String.format("User id %d not found", id)));
        }
    }

    @Override
    public List<User> getListAllUsers() {
        try {
            return jdbcTemplate.queryForObject("SELECT u.user_id, u.user_name, u.user_login, " +
                    "u.user_email, u.user_birthday, f.user2_id " +
                    "FROM users u " +
                    "LEFT JOIN friendships f ON u.user_id = f.user1_id", userRowMapper());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public User addUser(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> params = Map.of("user_name", user.getName(),
                "user_login", user.getLogin(),
                "user_email", user.getEmail(),
                "user_birthday", user.getBirthday());

        int id = simpleJdbcInsert.executeAndReturnKey(params).intValue();
        user.setId(id);
        return user;
    }

    @Override
    public User deleteUser(int id) {
        User user = getUserById(id);
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
        return user;
    }

/** По моему мнению, список лайков и друзей не должен обновляться при изменении данных пользователя.
 * Если требуется учитывать и их тоже, то исправлю.*/
    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        jdbcTemplate.update("UPDATE users " +
                "SET user_name = ?, " +
                "user_login = ?, " +
                "user_email = ?, " +
                "user_birthday = ? " +
                "WHERE user_id = ?",
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void addFriend(int id1, int id2) {
        getUserById(id1);
        getUserById(id2);

        jdbcTemplate.update("INSERT INTO friendships (user1_id, user2_id) " +
                "VALUES (?, ?)", id1, id2);

    }

    @Override
    public void deleteFriend(int id1, int id2) {
        getUserById(id1);
        getUserById(id2);

        jdbcTemplate.update("DELETE FROM friendships " +
                "WHERE user1_id = ? AND user2_id = ?", id1, id2);
    }

    @Override
    public List<User> listAllFriends(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT u.user_id, u.user_name, u.user_login, " +
                    "u.user_email, u.user_birthday, f2.user2_id from friendships f " +
                    "join users u on f.user2_id = u.user_id " +
                    "left join friendships f2 on u.user_id = f2.user1_id " +
                    "where f.user1_id = ?", userRowMapper(), id);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private RowMapper<List<User>> userRowMapper() {
        return (rs, rowNum) -> {
            List<User> users = new ArrayList<>();

            User user = new User(rs.getInt("user_id"), rs.getString("user_email"),
                    rs.getString("user_login"), rs.getString("user_name"),
                    rs.getDate("user_birthday").toLocalDate());
            do {
                if (user.getId() == rs.getInt("user_id")) {
                    if (rs.getInt("user2_id") != 0) {
                        user.getFriendsList().add(rs.getInt("user2_id"));
                    }
                } else {
                    user = new User(rs.getInt("user_id"), rs.getString("user_email"),
                            rs.getString("user_login"), rs.getString("user_name"),
                            rs.getDate("user_birthday").toLocalDate());
                }
                if (!users.stream()
                        .map(User::getId)
                        .collect(Collectors.toList()).contains(user.getId())) {
                    users.add(user);
                }
            } while (rs.next());
            return users;
        };
    }
}