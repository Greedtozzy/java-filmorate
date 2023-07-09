package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    FilmController controller;
    UserStorage userStorage;
    Film film;
    Validator validator;

    @BeforeEach
    public void testStartBeforeEach() {
        userStorage = new InMemoryUserStorage();
        controller = new FilmController(new FilmService(new InMemoryFilmStorage()));
        film = new Film(1, "name", "description",
                LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void addFilmWithCorrectFilmTest() throws ValidationException {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertTrue(controller.allFilms().contains(film));
        assertEquals(controller.getFilmById(1), film);
    }

    @Test
    public void addFilmWithNotCorrectFilmNameTest() throws ValidationException {
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.allFilms().contains(film));

        film.setName(null);
        violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.allFilms().contains(film));
    }

    @Test
    public void addFilmWithNotCorrectFilmDescriptionTest() throws ValidationException {
        film.setDescription("11010001100001011101000010111110110100011000000011010000101111101101000110001000110" +
                "10000101101011101000010110011110100001011111000100000110100001011110111010000101100001101000110" +
                "00000111010001100000101101000110000000110100001011111011010000101101011101000010111101110100001" +
                "01110001101000110001111");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.allFilms().contains(film));
    }

    @Test
    public void addFilmWithNotCorrectFilmReleaseDateTest() throws ValidationException {
        film.setReleaseDate(LocalDate.of(1895,12,27));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.allFilms().contains(film));

        film.setReleaseDate(LocalDate.now().plusDays(1));
        violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.allFilms().contains(film));
    }

    @Test
    public void addFilmWithNotCorrectFilmDurationTest() throws ValidationException {
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.allFilms().contains(film));

        film.setDuration(-1);
        violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.allFilms().contains(film));
    }

    @Test
    public void updateFilmWithCorrectFilmTest() {
        controller.addFilm(film);
        Film filmToUpdate = new Film(1, "anotherName", "anotherDescription",
                LocalDate.of(2000,1,2), 121, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertEquals(controller.getFilmById(1).getName(), filmToUpdate.getName());
        assertEquals(controller.getFilmById(1).getDescription(), filmToUpdate.getDescription());
        assertEquals(controller.getFilmById(1).getReleaseDate(), filmToUpdate.getReleaseDate());
        assertEquals(controller.getFilmById(1).getDuration(), filmToUpdate.getDuration());
    }

    @Test
    public void updateFilmWithNotCorrectFilmNameTest() {
        controller.addFilm(film);
        Film filmToUpdate = new Film(1, "", "anotherDescription",
                LocalDate.of(2000,1,2), 121, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(filmToUpdate);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotEquals("", controller.getFilmById(1).getName());

        filmToUpdate.setName(null);
        violations = validator.validate(filmToUpdate);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotNull(controller.getFilmById(1).getName());
    }

    @Test
    public void updateFilmWithNotCorrectFilmDescriptionTest() {
        controller.addFilm(film);
        Film filmToUpdate = new Film(1, "anotherName", "1101000110000101110100001011111011010001100" +
                "0000011010000101111101101000110001000110100001011010111010000101100111101000010111110001000001101000" +
                "0101111011101000010110000110100011000000111010001100000101101000110000000110100001011111011010000101" +
                "10101110100001011110111010000101110001101000110001111",
                LocalDate.of(2000,1,2), 121, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotEquals("", controller.getFilmById(1).getName());
    }

    @Test
    public void updateFilmWithNotCorrectFilmReleaseDateTest() {
        controller.addFilm(film);
        Film filmToUpdate = new Film(1, "anotherName", "anotherDescription",
                LocalDate.of(1895,12,27), 121, new Mpa(1, "G"));
        Set<ConstraintViolation<Film>> violations = validator.validate(filmToUpdate);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotEquals(filmToUpdate.getReleaseDate(), controller.getFilmById(1).getReleaseDate());

        filmToUpdate.setReleaseDate(LocalDate.now().plusDays(1));
        violations = validator.validate(filmToUpdate);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotEquals(filmToUpdate.getReleaseDate(), controller.getFilmById(1).getReleaseDate());
    }

    @Test
    public void updateFilmWithNotCorrectFilmDurationTest() {
        controller.addFilm(film);
        Film filmToUpdate = new Film(1, "anotherName", "anotherDescription",
                LocalDate.of(2000,1,2), 0, new Mpa(1, "G"));
        filmToUpdate.setId(1);
        filmToUpdate.setName("anotherName");
        filmToUpdate.setDescription("anotherDescription");
        filmToUpdate.setReleaseDate(LocalDate.of(2000,1,2));
        filmToUpdate.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(filmToUpdate);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotEquals(0, controller.getFilmById(1).getDuration());

        filmToUpdate.setDuration(-1);
        violations = validator.validate(filmToUpdate);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotEquals(-1, controller.getFilmById(1).getDuration());
    }

    @Test
    public void allFilmsTest() {
        assertTrue(controller.allFilms().isEmpty());
        controller.addFilm(film);
        assertTrue(controller.allFilms().contains(film));
        assertEquals(1, controller.allFilms().size());
    }

    @Test
    public void getFilmByIdTest() {
        controller.addFilm(film);
        assertEquals(controller.getFilmById(1), film);
    }

    @Test
    public void getFilmByInvalidId() {
        assertThrows(RuntimeException.class, () -> controller.getFilmById(-1));
        assertThrows(RuntimeException.class, () -> controller.getFilmById(5));
    }

    @Test
    public void addLikeTest() {
        controller.addFilm(film);
        User user = new User(1, "login", "name", "email@email.com",
                LocalDate.of(2000, 1, 1));
        userStorage.addUser(user);
        controller.addLike(1, 1);
        assertTrue(controller.getFilmById(1).getLikes().contains(1));
    }

    @Test
    public void deleteLikeTest() {
        controller.addFilm(film);
        User user = new User(1, "login", "name", "email@email.com",
                LocalDate.of(2000, 1, 1));
        userStorage.addUser(user);
        controller.addLike(1, 1);
        assertTrue(controller.getFilmById(1).getLikes().contains(1));
        controller.deleteLike(1, 1);
        assertTrue(controller.getFilmById(1).getLikes().isEmpty());
    }

    @Test
    public void getTopFilmsTest() {
        controller.addFilm(film);

        Film f1 = new Film(1,"1", "1", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f1);

        Film f2 = new Film(2,"2", "2", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f2);

        Film f3 = new Film(3,"3", "3", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f3);

        Film f4 = new Film(4,"4", "4", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f4);

        Film f5 = new Film(5,"5", "5", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f5);

        Film f6 = new Film(6,"6", "6", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f6);

        Film f7 = new Film(7,"7", "7", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f7);

        Film f8 = new Film(8,"8", "8", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f8);

        Film f9 = new Film(9,"9", "9", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f9);

        Film f10 = new Film(10,"10", "10", LocalDate.of(2000,1,1), 120, new Mpa(1, "G"));
        controller.addFilm(f10);

        User user = new User(1, "login", "name", "email@email.com",
                LocalDate.of(2000, 1, 1));
        userStorage.addUser(user);

        User user1 = new User(2, "login1", "name1", "email1@email.com",
                LocalDate.of(2000, 1, 1));
        userStorage.addUser(user1);

        controller.addLike(1, 1);
        controller.addLike(2, 1);
        controller.addLike(3, 1);
        controller.addLike(4, 1);
        controller.addLike(5, 1);
        controller.addLike(6, 1);
        controller.addLike(7, 1);
        controller.addLike(8, 1);
        controller.addLike(9, 1);
        controller.addLike(10, 1);
        controller.addLike(1, 2);
        controller.addLike(2, 2);

        assertEquals(10, controller.getTopFilms(10).size());
        assertFalse(controller.getTopFilms(10).contains(controller.getFilmById(11)));
        assertEquals(2, controller.getTopFilms(2).size());
        assertTrue(controller.getTopFilms(2).contains(film)
        && controller.getTopFilms(2).contains(f1));
    }
}
