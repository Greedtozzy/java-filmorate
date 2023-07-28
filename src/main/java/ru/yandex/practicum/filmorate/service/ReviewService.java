package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    public Review addReview(Review review) {
        Review newReview = reviewStorage.addReview(review);
        eventStorage.addEvent(newReview.getReviewId(), EventType.REVIEW, EventOperation.ADD, newReview.getUserId());
        return newReview;
    }

    public Review updateReview(Review review) {
        Review updatedReview = reviewStorage.updateReview(review);
        eventStorage.addEvent(updatedReview.getReviewId(), EventType.REVIEW, EventOperation.UPDATE, updatedReview.getUserId());
        return updatedReview;
    }

    public void deleteReview(int id) {
        Review review = reviewStorage.getReviewById(id);
        eventStorage.addEvent(review.getReviewId(), EventType.REVIEW, EventOperation.REMOVE, review.getUserId());
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
