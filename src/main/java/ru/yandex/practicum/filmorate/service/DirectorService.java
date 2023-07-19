package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getListAllDirectors(){
        return directorStorage.getListAllDirectors();
    }

    public Director getDirectorById(int id){
        return directorStorage.getDirectorById(id);
    }

    public Director addDirector(Director director){
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director){
        return directorStorage.updateDirector(director);
    }

    public Director deleteDirector(int id){
        return directorStorage.deleteDirector(id);
    }

}
