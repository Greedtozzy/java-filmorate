package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(int id) {
        reviewStorage.deleteReview(id);
    }

    public List<Review> getReviewListWithParam(int filmId, int count) {
        return reviewStorage.getReviewListWithParam(filmId, count);
    }

    public List<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id);
    }

    public Review addLike(int reviewId, int userId) {
        return reviewStorage.addLikeToReview(reviewId, userId);
    }

    public Review deleteLike(int reviewId, int userId) {
        return reviewStorage.deleteLikeFromReview(reviewId, userId);
    }

    public Review addDislike(int reviewId, int userId) {
        return reviewStorage.addDislikeToReview(reviewId, userId);
    }

    public Review deleteDislike(int reviewId, int userId) {
        return reviewStorage.deleteDislikeFromReview(reviewId, userId);
    }
}
