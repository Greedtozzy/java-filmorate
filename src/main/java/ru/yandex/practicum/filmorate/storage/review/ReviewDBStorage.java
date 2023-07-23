package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ReviewDBStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDBStorage filmDBStorage;
    private final UserDBStorage userDBStorage;

    @Override
    public Review addReview(Review review) {
        filmDBStorage.getFilmById(review.getFilmId());
        userDBStorage.getUserById(review.getUserId());
        String sqlQuery = "INSERT INTO review (review_content, review_is_positive, " +
                "user_id, film_id, review_useful) values (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            stmt.setInt(5, review.getUseful());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().intValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE review SET " +
                "review_content = ?, " +
                "review_is_positive = ?, " +
                "review_useful = ? " +
                "WHERE review_id = ?;";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(int id) {
        jdbcTemplate.update("DELETE FROM review WHERE review_id = ?;", id);
    }

    @Override
    public List<Review> getReviewListWithParam(int filmId, int count) {
        try {
            if (filmId == 0) {
                String sqlQuery = "SELECT * FROM review ORDER BY useful DESC LIMIT ?;";
                return jdbcTemplate.queryForObject(sqlQuery, reviewRowMapper(), count);
            }
            String sqlQuery = "SELECT * FROM review WHERE film_id = ?  ORDER BY useful DESC" +
                    " LIMIT ?;";
            return jdbcTemplate.queryForObject(sqlQuery, reviewRowMapper(), filmId, count);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Review> getAllReviews() {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM review;", reviewRowMapper())
                    .stream()
                    .sorted(Comparator.comparing(Review::getUseful))
                    .collect(Collectors.toList());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    @Override
    public Review getReviewById(int id) {
        try {
            String sqlQuery = "SELECT review_id, review_content, review_is_positive, " +
                    "user_id, film_id, review_useful FROM review WHERE review_id = ?;";
            List<Review> reviews = jdbcTemplate.queryForObject(sqlQuery, reviewRowMapper(), id);
            return reviews.get(0);
        } catch (EmptyResultDataAccessException e) {
            throw new ReviewNotFoundException(String.format("Review id %d not found", id));
        }
    }

    private RowMapper<List<Review>> reviewRowMapper() {
        return (rs, rowNum) -> {
            List<Review> reviews = new ArrayList<>();
            Review review = new Review(rs.getInt("review_id"),
                    rs.getString("review_content"),
                    rs.getBoolean("review_is_positive"),
                    rs.getInt("user_id"),
                    rs.getInt("film_id"),
                    rs.getInt(("review_useful")));
            do {
                reviews.add(review);
            } while (rs.next());
            return reviews;
        };
    }

    @Override
    public Review addLikeToReview(int reviewId, int userId) {
        jdbcTemplate.update("UPDATE review SET useful = useful + 1 WHERE review_id = ? AND user_id = ?;",
                reviewId, userId);
        return getReviewById(reviewId);
    }

    @Override
    public Review addDislikeToReview(int reviewId, int userId) {
        jdbcTemplate.update("UPDATE review SET useful = useful - 1 WHERE review_id = ? AND user_id = ?;",
                reviewId, userId);
        return getReviewById(reviewId);
    }

    @Override
    public Review deleteLikeFromReview(int reviewId, int userId) {
        jdbcTemplate.update("UPDATE review SET useful = useful - 1 WHERE review_id = ? AND user_id = ?;",
                reviewId, userId);
        return getReviewById(reviewId);
    }

    @Override
    public Review deleteDislikeFromReview(int reviewId, int userId) {
        jdbcTemplate.update("UPDATE review SET useful = useful + 1 WHERE review_id = ? AND user_id = ?;",
                reviewId, userId);
        return getReviewById(reviewId);
    }
}
