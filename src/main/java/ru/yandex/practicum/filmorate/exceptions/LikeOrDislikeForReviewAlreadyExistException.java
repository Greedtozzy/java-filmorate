package ru.yandex.practicum.filmorate.exceptions;

public class LikeOrDislikeForReviewAlreadyExistException extends RuntimeException {
    public LikeOrDislikeForReviewAlreadyExistException(String message) {
        super(message);
    }
}
