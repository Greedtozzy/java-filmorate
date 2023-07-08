package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
    private int id;
    private String name;

    public void setId(int id) {
        this.id = id;
        switch (id) {
            case 1:
                name = "Комедия";
                break;
            case 2:
                name = "Драма";
                break;
            case 3:
                name = "Мультфильм";
                break;
            case 4:
                name = "Триллер";
                break;
            case 5:
                name = "Документальный";
                break;
            case 6:
                name = "Боевик";
                break;
            default:
                name = null;
        }
    }
}
