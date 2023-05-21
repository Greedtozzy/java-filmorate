package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
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
    public void createUserWithCorrectUserTest() throws ValidationException {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertTrue(controller.users.containsValue(user));
        assertEquals(controller.users.get(1), user);
    }

    @Test
    public void createUserWithNotCorrectUsersEmailTest() throws ValidationException {
        user.setEmail("emailEmail");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.users.containsValue(user));
    }

    @Test
    public void createUserWithNotCorrectUsersLoginTest() throws ValidationException {
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.users.containsValue(user));
        user.setLogin("log in");
        violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.users.containsValue(user));
        user.setLogin(null);
        violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.users.containsValue(user));
    }

    @Test
    public void createUserWithNotCorrectUsersBirthdayTest() throws ValidationException {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertFalse(controller.users.containsValue(user));
    }

    @Test
    public void createUserWithBlankNameTest() throws ValidationException {
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertEquals(user.getLogin(), controller.users.get(1).getName());
    }

    @Test
    public void createUserWithNullNameTest() throws ValidationException {
        user.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.createUser(user);
        }
        assertEquals(user.getLogin(), controller.users.get(1).getName());
    }

    @Test
    public void updateUserWithCorrectUserTest() throws ValidationException {
        controller.createUser(user);
        user.setName("anotherName");
        user.setLogin("anotherLogin");
        user.setEmail("anotherEmail@email.com");
        user.setBirthday(user.getBirthday().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) {
            controller.updateUser(user);
        }

        assertEquals(controller.users.get(1).getName(), "anotherName");
        assertEquals(controller.users.get(1).getLogin(), "anotherLogin");
        assertEquals(controller.users.get(1).getBirthday(), LocalDate.of(2000, 1, 2));
        assertEquals(controller.users.get(1).getEmail(), "anotherEmail@email.com");
    }

    @Test
    public void updateUserWithNotCorrectUsersEmailTest() throws ValidationException {
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
        assertNotEquals("anotherEmailEmail.com", controller.users.get(1).getEmail());
    }

    @Test
    public void updateUserWithNotCorrectUsersLoginTest() throws ValidationException {
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
        assertNotEquals("", controller.users.get(1).getLogin());
        userForUpdate.setLogin("log in");
        violations = validator.validate(userForUpdate);
        if (violations.isEmpty()) {
            controller.updateUser(userForUpdate);
        }
        assertNotEquals("", controller.users.get(1).getLogin());
    }

    @Test
    public void updateUserWithNotCorrectUsersBirthdayTest() throws ValidationException {
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
        assertNotEquals(controller.users.get(1).getBirthday(), LocalDate.now().plusDays(1));
    }

    @Test
    public void updateUserWithBlankNameTest() throws ValidationException {
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
        assertEquals(userForUpdate.getLogin(), controller.users.get(1).getName());
    }

    @Test
    public void updateUserWithNullNameTest() throws ValidationException {
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
        assertEquals(userForUpdate.getLogin(), controller.users.get(1).getName());
    }

    @Test
    public void allUsersListTest() throws ValidationException {
        assertTrue(controller.allUsers().isEmpty());
        controller.createUser(user);
        assertTrue(controller.allUsers().contains(user));
        assertEquals(1, controller.allUsers().size());
    }
}
