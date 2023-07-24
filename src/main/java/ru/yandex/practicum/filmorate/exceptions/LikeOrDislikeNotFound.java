package ru.yandex.practicum.filmorate.exceptions;

public class LikeOrDislikeNotFound extends RuntimeException{
    public LikeOrDislikeNotFound(String message) {
        super(message);
    }
}
