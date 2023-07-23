package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int id);

    List<Review> getReviewListWithParam(int filmId, int count);

    List<Review> getAllReviews();

    Review getReviewById(int id);

    Review addLikeToReview(int reviewId, int userId);

    Review addDislikeToReview(int reviewId, int userId);

    Review deleteLikeFromReview(int reviewId, int userId);

    Review deleteDislikeFromReview(int reviewId, int userId);
}
