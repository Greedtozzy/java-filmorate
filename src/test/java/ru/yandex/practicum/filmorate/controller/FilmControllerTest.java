package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage));
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000,1,1));
        film.setDuration(120);
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
        Film filmToUpdate = new Film();
        filmToUpdate.setId(1);
        filmToUpdate.setName("anotherName");
        filmToUpdate.setDescription("anotherDescription");
        filmToUpdate.setReleaseDate(LocalDate.of(2000,1,2));
        filmToUpdate.setDuration(121);
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
        Film filmToUpdate = new Film();
        filmToUpdate.setId(1);
        filmToUpdate.setName("");
        filmToUpdate.setDescription("anotherDescription");
        filmToUpdate.setReleaseDate(LocalDate.of(2000,1,2));
        filmToUpdate.setDuration(121);
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
        Film filmToUpdate = new Film();
        filmToUpdate.setId(1);
        filmToUpdate.setName("anotherName");
        filmToUpdate.setDescription("11010001100001011101000010111110110100011000000011010000101111101101000" +
                "1100010001101000010110101110100001011001111010000101111100010000011010000101111011101000010" +
                "1100001101000110000001110100011000001011010001100000001101000010111110110100001011010111010" +
                "0001011110111010000101110001101000110001111");
        filmToUpdate.setReleaseDate(LocalDate.of(2000,1,2));
        filmToUpdate.setDuration(121);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotEquals("", controller.getFilmById(1).getName());
    }

    @Test
    public void updateFilmWithNotCorrectFilmReleaseDateTest() {
        controller.addFilm(film);
        Film filmToUpdate = new Film();
        filmToUpdate.setId(1);
        filmToUpdate.setName("anotherName");
        filmToUpdate.setDescription("anotherDescription");
        filmToUpdate.setReleaseDate(LocalDate.of(1895,12,27));
        filmToUpdate.setDuration(121);
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
        Film filmToUpdate = new Film();
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
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("email@email.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.addUser(user);
        controller.addLike(1, 1);
        assertTrue(controller.getFilmById(1).getLikes().contains(1));
    }

    @Test
    public void deleteLikeTest() {
        controller.addFilm(film);
        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("email@email.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.addUser(user);
        controller.addLike(1, 1);
        assertTrue(controller.getFilmById(1).getLikes().contains(1));
        controller.deleteLike(1, 1);
        assertTrue(controller.getFilmById(1).getLikes().isEmpty());
    }

    @Test
    public void getTopFilmsTest() {
        controller.addFilm(film);

        Film f1 = new Film();
        f1.setName("1");
        f1.setDescription("1");
        f1.setReleaseDate(LocalDate.of(2000,1,1));
        f1.setDuration(120);
        controller.addFilm(f1);

        Film f2 = new Film();
        f2.setName("2");
        f2.setDescription("2");
        f2.setReleaseDate(LocalDate.of(2000,1,1));
        f2.setDuration(120);
        controller.addFilm(f2);

        Film f3 = new Film();
        f3.setName("3");
        f3.setDescription("3");
        f3.setReleaseDate(LocalDate.of(2000,1,1));
        f3.setDuration(120);
        controller.addFilm(f3);

        Film f4 = new Film();
        f4.setName("4");
        f4.setDescription("4");
        f4.setReleaseDate(LocalDate.of(2000,1,1));
        f4.setDuration(120);
        controller.addFilm(f4);

        Film f5 = new Film();
        f5.setName("5");
        f5.setDescription("5");
        f5.setReleaseDate(LocalDate.of(2000,1,1));
        f5.setDuration(120);
        controller.addFilm(f5);

        Film f6 = new Film();
        f6.setName("6");
        f6.setDescription("6");
        f6.setReleaseDate(LocalDate.of(2000,1,1));
        f6.setDuration(120);
        controller.addFilm(f6);

        Film f7 = new Film();
        f7.setName("7");
        f7.setDescription("7");
        f7.setReleaseDate(LocalDate.of(2000,1,1));
        f7.setDuration(120);
        controller.addFilm(f7);

        Film f8 = new Film();
        f8.setName("8");
        f8.setDescription("8");
        f8.setReleaseDate(LocalDate.of(2000,1,1));
        f8.setDuration(120);
        controller.addFilm(f8);

        Film f9 = new Film();
        f9.setName("9");
        f9.setDescription("9");
        f9.setReleaseDate(LocalDate.of(2000,1,1));
        f9.setDuration(120);
        controller.addFilm(f9);

        Film f10 = new Film();
        f10.setName("10");
        f10.setDescription("10");
        f10.setReleaseDate(LocalDate.of(2000,1,1));
        f10.setDuration(120);
        controller.addFilm(f10);

        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setEmail("email@email.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.addUser(user);

        User user1 = new User();
        user1.setLogin("login1");
        user1.setName("name1");
        user1.setEmail("email1@email.com");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
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
