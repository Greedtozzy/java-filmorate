package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    FilmController controller;
    Film film;
    Validator validator;

    @BeforeEach
    public void testStartBeforeEach() {
        controller = new FilmController();
        film = new Film();
        film.setId(1);
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
        assertTrue(controller.getFilms().containsValue(film));
        assertEquals(controller.getFilms().get(1), film);
    }

    @Test
    public void addFilmWithNotCorrectFilmNameTest() throws ValidationException {
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.getFilms().containsValue(film));

        film.setName(null);
        violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.getFilms().containsValue(film));
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
        assertFalse(controller.getFilms().containsValue(film));
    }

    @Test
    public void addFilmWithNotCorrectFilmReleaseDateTest() throws ValidationException {
        film.setReleaseDate(LocalDate.of(1895,12,27));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.getFilms().containsValue(film));

        film.setReleaseDate(LocalDate.now().plusDays(1));
        violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.getFilms().containsValue(film));
    }

    @Test
    public void addFilmWithNotCorrectFilmDurationTest() throws ValidationException {
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.getFilms().containsValue(film));

        film.setDuration(-1);
        violations = validator.validate(film);
        if (violations.isEmpty()) {
            controller.addFilm(film);
        }
        assertFalse(controller.getFilms().containsValue(film));
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
        assertEquals(controller.getFilms().get(1).getName(), filmToUpdate.getName());
        assertEquals(controller.getFilms().get(1).getDescription(), filmToUpdate.getDescription());
        assertEquals(controller.getFilms().get(1).getReleaseDate(), filmToUpdate.getReleaseDate());
        assertEquals(controller.getFilms().get(1).getDuration(), filmToUpdate.getDuration());
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
        assertNotEquals("", controller.getFilms().get(1).getName());

        filmToUpdate.setName(null);
        violations = validator.validate(filmToUpdate);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotNull(controller.getFilms().get(1).getName());
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
        assertNotEquals("", controller.getFilms().get(1).getName());
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
        assertNotEquals(filmToUpdate.getReleaseDate(), controller.getFilms().get(1).getReleaseDate());

        filmToUpdate.setReleaseDate(LocalDate.now().plusDays(1));
        violations = validator.validate(filmToUpdate);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotEquals(filmToUpdate.getReleaseDate(), controller.getFilms().get(1).getReleaseDate());
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
        assertNotEquals(0, controller.getFilms().get(1).getDuration());

        filmToUpdate.setDuration(-1);
        violations = validator.validate(filmToUpdate);
        if (violations.isEmpty()) {
            controller.updateFilm(filmToUpdate);
        }
        assertNotEquals(-1, controller.getFilms().get(1).getDuration());
    }

    @Test
    public void allFilmsTest() {
        assertTrue(controller.allFilms().isEmpty());
        controller.addFilm(film);
        assertTrue(controller.allFilms().contains(film));
        assertEquals(1, controller.allFilms().size());
    }
}
