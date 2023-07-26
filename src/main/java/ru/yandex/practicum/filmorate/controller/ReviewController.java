package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.debug("Review {} add", review);
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.debug("Review {} update", review);
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        log.debug("Review by id {} delete", id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        log.debug("Review by id {}", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewList(@RequestParam(value = "filmId", defaultValue = "0", required = false)
                                      int filmId,
                                      @RequestParam(value = "count", defaultValue = "10", required = false)
                                      int count) {
        log.debug("Review list of films count {}", count);
        return reviewService.getReviewListWithParam(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("User id {} add like film id {}", userId, id);
        return reviewService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.debug("User id {} delete like film id {}", userId, id);
        return reviewService.deleteLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable int id, @PathVariable int userId) {
        log.debug("User id {} add dislike film id {}", userId, id);
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislike(@PathVariable int id, @PathVariable int userId) {
        log.debug("User id {} delete dislike film id {}", userId, id);
        return reviewService.deleteDislike(id, userId);
    }
}

