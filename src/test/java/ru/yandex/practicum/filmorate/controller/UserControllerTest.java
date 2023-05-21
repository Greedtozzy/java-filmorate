package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

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
        controller = new UserController();
        user = new User();
        user.setId(1);
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
        assertTrue(controller.getUsers().containsValue(user));
        assertEquals(controller.getUsers().get(1), user);
    }

    @Test
    public void createUserWithNotCorrectUsersEmailTest() {
        user.setEmail("emailEmail");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.getUsers().containsValue(user));
    }

    @Test
    public void createUserWithNotCorrectUsersLoginTest() {
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.getUsers().containsValue(user));
        user.setLogin("log in");
        violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.getUsers().containsValue(user));
        user.setLogin(null);
        violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.getUsers().containsValue(user));
    }

    @Test
    public void createUserWithNotCorrectUsersBirthdayTest() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.getUsers().containsValue(user));
    }

    @Test
    public void createUserWithBlankNameTest() {
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertEquals(user.getLogin(), controller.getUsers().get(1).getName());
    }

    @Test
    public void createUserWithNullNameTest() {
        user.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertEquals(user.getLogin(), controller.getUsers().get(1).getName());
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

        assertEquals(controller.getUsers().get(1).getName(), "anotherName");
        assertEquals(controller.getUsers().get(1).getLogin(), "anotherLogin");
        assertEquals(controller.getUsers().get(1).getBirthday(), LocalDate.of(2000, 1, 2));
        assertEquals(controller.getUsers().get(1).getEmail(), "anotherEmail@email.com");
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
        assertNotEquals("anotherEmailEmail.com", controller.getUsers().get(1).getEmail());
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
        assertNotEquals("", controller.getUsers().get(1).getLogin());
        userForUpdate.setLogin("log in");
        violations = validator.validate(userForUpdate);
        if (violations.isEmpty()) {
            controller.updateUser(userForUpdate);
        }
        assertNotEquals("", controller.getUsers().get(1).getLogin());
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
        assertNotEquals(controller.getUsers().get(1).getBirthday(), LocalDate.now().plusDays(1));
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
        assertEquals(userForUpdate.getLogin(), controller.getUsers().get(1).getName());
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
        assertEquals(userForUpdate.getLogin(), controller.getUsers().get(1).getName());
    }

    @Test
    public void allUsersListTest() {
        assertTrue(controller.allUsers().isEmpty());
        controller.createUser(user);
        assertTrue(controller.allUsers().contains(user));
        assertEquals(1, controller.allUsers().size());
    }
}
