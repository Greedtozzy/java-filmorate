package ru.yandex.practicum.filmorate.exceptions;

public class FriendshipsException extends RuntimeException {
    public FriendshipsException(String message) {
        super(message);
    }

    public FriendshipsException(String message, Throwable cause) {
        super(message, cause);
    }
}
