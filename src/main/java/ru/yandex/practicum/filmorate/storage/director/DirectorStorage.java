package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> getListAllDirectors();

    Director getDirectorById(int id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    Director deleteDirector(int id);
}
