package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
	private final UserDBStorage us;
	private final FilmDBStorage fs;
	private User user;
	private Film film;

	@BeforeEach
	public void beforeEach() {
		user = new User(1, "e@e.com", "login", "name",
				LocalDate.of(2000, 1, 1));
		film = new Film(1, "name", "description",
				LocalDate.of(2000, 1, 1), 120);
	}

	@Test
	public void testAddUser() {
		Optional<User> userOptional = Optional.ofNullable(us.addUser(user));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(u ->
						assertThat(u).hasFieldOrPropertyWithValue("id", 1)
				);
		assertEquals(user, us.getUserById(1));
	}

	@Test
	public void testFindUserById() {
		us.addUser(user);

		Optional<User> userOptional = Optional.ofNullable(us.getUserById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testAllUsers() {
		us.addUser(user);
		List<User> users = us.getListAllUsers();
		assertEquals(users.size(), 1);
		assertEquals(user, users.get(0));
	}

	@Test
	public void testDeleteUser() {
		us.addUser(user);
		assertEquals(us.getListAllUsers().size(), 1);
		us.deleteUser(1);
		assertEquals(us.getListAllUsers().size(), 0);
	}

	@Test
	public void testUpdateUser() {
		us.addUser(user);
		User userForUpdat = new User(1, "e1@e.com", "login1", "name1",
				LocalDate.of(1990, 1,1));
		us.updateUser(userForUpdat);
		assertEquals(us.getUserById(1), userForUpdat);
	}

	@Test
	public void testAddFriend() {
		us.addUser(user);
		User user1 = new User(2, "e1@e.com", "login1", "name1",
				LocalDate.of(1990, 1,1));
		us.addUser(user1);
		us.addFriend(1, 2);
		assertTrue(us.getUserById(1).getFriendsList().contains(2));
		assertEquals(us.getUserById(1).getFriendsList().size(), 1);
	}

	@Test
	public void testListAllFriends() {
		us.addUser(user);
		User user1 = new User(2, "e1@e.com", "login1", "name1",
				LocalDate.of(1990, 1,1));
		us.addUser(user1);
		us.addFriend(1, 2);
		assertEquals(us.listAllFriends(1).size(), 1);
		assertEquals(us.listAllFriends(1).get(0), user1);
	}

	@Test
	public void testDeleteFriend() {
		us.addUser(user);
		User user1 = new User(2, "e1@e.com", "login1", "name1",
				LocalDate.of(1990, 1,1));
		us.addUser(user1);
		us.addFriend(1, 2);
		assertEquals(us.getUserById(1).getFriendsList().size(), 1);
		us.deleteFriend(1, 2);
		Set<Integer> fl = us.getUserById(1).getFriendsList();
		assertEquals(us.getUserById(1).getFriendsList().size(), 0);
	}

	@Test
	public void testAddFilm() {
		fs.addFilm(film);
		assertEquals(film, fs.getFilmById(1));
	}

	@Test
	public void testGetFilmById() {
		fs.addFilm(film);
		Optional<Film> userOptional = Optional.ofNullable(fs.getFilmById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(f ->
						assertThat(f).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	public void testGetListAllFilms() {
		fs.addFilm(film);
		List<Film> films = fs.getListAllFilms();
		assertEquals(films.size(), 1);
		assertEquals(films.get(0), film);
	}

	@Test
	public void testUpdateFilm() {
		fs.addFilm(film);
		Film filmForUpdate = new Film(1, "name1", "description1",
				LocalDate.of(2010, 1, 1), 300);
		fs.updateFilm(filmForUpdate);
		assertEquals(fs.getFilmById(1), filmForUpdate);
	}

	@Test
	public void testDeleteFilm() {
		fs.addFilm(film);
		assertEquals(fs.getListAllFilms().size(), 1);
		fs.deleteFilm(1);
		assertEquals(fs.getListAllFilms().size(), 0);
	}

	@Test
	public void testAddLike() {
		fs.addFilm(film);
		us.addUser(user);
		fs.addLike(1, 1);
		assertEquals(fs.getFilmById(1).getRate(), 1);
	}

	@Test
	public void testDeleteLike() {
		fs.addFilm(film);
		us.addUser(user);
		fs.addLike(1, 1);
		assertEquals(fs.getFilmById(1).getRate(), 1);
		fs.deleteLike(1, 1);
		assertEquals(fs.getFilmById(1).getRate(), 0);
	}

	@Test
	public void testGetGenres() {
		List<Genre> genres = fs.getGenres();
		assertEquals(genres.size(), 6);;
	}

	@Test
	public void testGetGenreById() {
		Genre genre = fs.getGenreById(1);
		assertEquals(1, genre.getId());
		assertEquals("Комедия", genre.getName());
	}

	@Test
	public void testGetRatings() {
		List<Mpa> ratings = fs.getRatings();
		assertEquals(ratings.size(), 5);
	}

	@Test
	public void testGetRatingById() {
		Mpa mpa = fs.getRatingById(1);
		assertEquals(1, mpa.getId());
		assertEquals("G", mpa.getName());
	}

	@Test
	public void testTopFilms() {
		fs.addFilm(film);
		Film film1 = new Film(2, "name1", "description1",
				LocalDate.of(2010, 1, 1), 300);
		fs.addFilm(film1);
		us.addUser(user);
		fs.addLike(1, 1);
		List<Film> top1 = fs.topFilms(1);
		assertEquals(top1.get(0), fs.getFilmById(1));
		assertEquals(1, top1.size());
		List<Film> top2 = fs.topFilms(2);
		assertEquals(2, top2.size());
		assertEquals(top2.get(1), fs.getFilmById(2));
	}
}
