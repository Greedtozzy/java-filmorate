package ru.yandex.practicum.filmorate.exceptions;

public class MpaNotFoundException extends RuntimeException {

    public MpaNotFoundException(String message) {
        super(message);
    }

    public MpaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
