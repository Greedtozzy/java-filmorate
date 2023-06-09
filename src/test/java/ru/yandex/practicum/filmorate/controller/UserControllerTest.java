package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController controller;
    User user;
    Validator validator;


    @BeforeEach
    public void testStartBeforeEach() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
        user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("email@email.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void createUserWithCorrectUserTest() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertTrue(controller.allUsers().contains(user));
        assertEquals(controller.getUserById(1), user);
    }

    @Test
    public void createUserWithNotCorrectUsersEmailTest() {
        user.setEmail("emailEmail");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.allUsers().contains(user));
    }

    @Test
    public void createUserWithNotCorrectUsersLoginTest() {
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.allUsers().contains(user));
        user.setLogin("log in");
        violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.allUsers().contains(user));
        user.setLogin(null);
        violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.allUsers().contains(user));
    }

    @Test
    public void createUserWithNotCorrectUsersBirthdayTest() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.allUsers().contains(user));
    }

    @Test
    public void createUserWithBlankNameTest() {
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertEquals(user.getLogin(), controller.getUserById(1).getName());
    }

    @Test
    public void createUserWithNullNameTest() {
        user.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertEquals(user.getLogin(), controller.getUserById(1).getName());
    }

    @Test
    public void updateUserWithCorrectUserTest() {
        controller.createUser(user);
        user.setName("anotherName");
        user.setLogin("anotherLogin");
        user.setEmail("anotherEmail@email.com");
        user.setBirthday(user.getBirthday().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.updateUser(user);
        }

        assertEquals(controller.getUserById(1).getName(), "anotherName");
        assertEquals(controller.getUserById(1).getLogin(), "anotherLogin");
        assertEquals(controller.getUserById(1).getBirthday(), LocalDate.of(2000, 1, 2));
        assertEquals(controller.getUserById(1).getEmail(), "anotherEmail@email.com");
    }

    @Test
    public void updateUserWithNotCorrectUsersEmailTest() {
        controller.createUser(user);
        User userForUpdate = new User();
        userForUpdate.setId(1);
        userForUpdate.setName("anotherName");
        userForUpdate.setLogin("anotherLogin");
        userForUpdate.setEmail("anotherEmailEmail.com");
        userForUpdate.setBirthday(LocalDate.of(2000,1,1));
        Set<ConstraintViolation<User>> violations = validator.validate(userForUpdate);
        if (violations.isEmpty()) {
            controller.updateUser(userForUpdate);
        }
        assertNotEquals("anotherEmailEmail.com", controller.getUserById(1).getEmail());
    }

    @Test
    public void updateUserWithNotCorrectUsersLoginTest() {
        controller.createUser(user);
        User userForUpdate = new User();
        userForUpdate.setId(1);
        userForUpdate.setName("anotherName");
        userForUpdate.setLogin("");
        userForUpdate.setEmail("anotherEmail@email.com");
        userForUpdate.setBirthday(LocalDate.of(2000,1,1));
        Set<ConstraintViolation<User>> violations = validator.validate(userForUpdate);
        if (violations.isEmpty()) {
            controller.updateUser(userForUpdate);
        }
        assertNotEquals("", controller.getUserById(1).getLogin());
        userForUpdate.setLogin("log in");
        violations = validator.validate(userForUpdate);
        if (violations.isEmpty()) {
            controller.updateUser(userForUpdate);
        }
        assertNotEquals("", controller.getUserById(1).getLogin());
    }

    @Test
    public void updateUserWithNotCorrectUsersBirthdayTest() {
        controller.createUser(user);
        User userForUpdate = new User();
        userForUpdate.setId(1);
        userForUpdate.setName("anotherName");
        userForUpdate.setLogin("anotherLogin");
        userForUpdate.setEmail("anotherEmail@email.com");
        userForUpdate.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(userForUpdate);
        if (violations.isEmpty()) {
            controller.updateUser(userForUpdate);
        }
        assertNotEquals(controller.getUserById(1).getBirthday(), LocalDate.now().plusDays(1));
    }

    @Test
    public void updateUserWithBlankNameTest() {
        controller.createUser(user);
        User userForUpdate = new User();
        userForUpdate.setId(1);
        userForUpdate.setName("");
        userForUpdate.setLogin("anotherLogin");
        userForUpdate.setEmail("anotherEmail@email.com");
        userForUpdate.setBirthday(LocalDate.of(2000,1,1));
        Set<ConstraintViolation<User>> violations = validator.validate(userForUpdate);
        if (violations.isEmpty()) {
            controller.updateUser(userForUpdate);
        }
        assertEquals(userForUpdate.getLogin(), controller.getUserById(1).getName());
    }

    @Test
    public void updateUserWithNullNameTest() {
        controller.createUser(user);
        User userForUpdate = new User();
        userForUpdate.setId(1);
        userForUpdate.setName(null);
        userForUpdate.setLogin("anotherLogin");
        userForUpdate.setEmail("anotherEmail@email.com");
        userForUpdate.setBirthday(LocalDate.of(2000,1,1));
        Set<ConstraintViolation<User>> violations = validator.validate(userForUpdate);
        if (violations.isEmpty()) {
            controller.updateUser(userForUpdate);
        }
        assertEquals(userForUpdate.getLogin(), controller.getUserById(1).getName());
    }

    @Test
    public void allUsersListTest() {
        assertTrue(controller.allUsers().isEmpty());
        controller.createUser(user);
        assertTrue(controller.allUsers().contains(user));
        assertEquals(1, controller.allUsers().size());
    }

    @Test
    public void getUserByIdTest() {
        controller.createUser(user);
        user.setId(1);
        assertEquals(controller.getUserById(1), user);
    }

    @Test
    public void getUserByInvalidId() {
        assertThrows(RuntimeException.class, () -> controller.getUserById(-1));
        assertThrows(RuntimeException.class, () -> controller.getUserById(5));
    }

    @Test
    public void deleteUserTest() {
        controller.createUser(user);
        user.setId(1);
        assertTrue(controller.allUsers().contains(user));
        controller.deleteUser(1);
        assertTrue(controller.allUsers().isEmpty());
    }

    @Test
    public void deleteUserByInvalidId() {
        assertThrows(RuntimeException.class, () -> controller.deleteUser(1));
    }

    @Test
    public void addFriendTest() {
        controller.createUser(user);
        User anotherUser = new User();
        anotherUser.setName("anotherName");
        anotherUser.setLogin("anotherLogin");
        anotherUser.setEmail("anotherEmail@email.com");
        anotherUser.setBirthday(LocalDate.of(2000,1,1));
        controller.createUser(anotherUser);
        controller.addFriend(1, 2);
        assertTrue(controller.getUserById(1).getFriendsList().contains(2));
        assertTrue(controller.getUserById(2).getFriendsList().contains(1));
    }

    @Test
    public void deleteFriendTest() {
        controller.createUser(user);
        User anotherUser = new User();
        anotherUser.setName("anotherName");
        anotherUser.setLogin("anotherLogin");
        anotherUser.setEmail("anotherEmail@email.com");
        anotherUser.setBirthday(LocalDate.of(2000,1,1));
        controller.createUser(anotherUser);
        controller.addFriend(1, 2);
        controller.deleteFriend(1, 2);
        assertTrue(controller.getUserById(1).getFriendsList().isEmpty());
        assertTrue(controller.getUserById(2).getFriendsList().isEmpty());
    }

    @Test
    public void listAllFriendsTest() {
        controller.createUser(user);
        User anotherUser = new User();
        anotherUser.setName("anotherName");
        anotherUser.setLogin("anotherLogin");
        anotherUser.setEmail("anotherEmail@email.com");
        anotherUser.setBirthday(LocalDate.of(2000,1,1));
        controller.createUser(anotherUser);
        User anotherUser1 = new User();
        anotherUser1.setName("anotherName1");
        anotherUser1.setLogin("anotherLogin1");
        anotherUser1.setEmail("anotherEmail1@email.com");
        anotherUser1.setBirthday(LocalDate.of(2000,1,1));
        controller.createUser(anotherUser1);
        controller.addFriend(1, 2);
        controller.addFriend(1, 3);
        assertTrue(controller.listAllFriends(1).contains(controller.getUserById(2))
                && controller.listAllFriends(1).contains(controller.getUserById(3)));
    }

    @Test
    public void commonFriendsTest() {
        controller.createUser(user);
        User anotherUser = new User();
        anotherUser.setName("anotherName");
        anotherUser.setLogin("anotherLogin");
        anotherUser.setEmail("anotherEmail@email.com");
        anotherUser.setBirthday(LocalDate.of(2000,1,1));
        controller.createUser(anotherUser);
        User anotherUser1 = new User();
        anotherUser1.setName("anotherName1");
        anotherUser1.setLogin("anotherLogin1");
        anotherUser1.setEmail("anotherEmail1@email.com");
        anotherUser1.setBirthday(LocalDate.of(2000,1,1));
        controller.createUser(anotherUser1);
        controller.addFriend(2, 3);
        controller.addFriend(1, 3);
        assertTrue(controller.commonFriends(1, 2).contains(controller.getUserById(3)));
    }
}
