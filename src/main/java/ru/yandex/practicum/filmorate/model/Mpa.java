package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {
    private int id;
    private String name;

    public void setId(int id) {
        this.id = id;
        switch (id) {
            case 1:
                name = "G";
                break;
            case 2:
                name = "PG";
                break;
            case 3:
                name = "PG-13";
                break;
            case 4:
                name = "R";
                break;
            case 5:
                name = "NC-17";
                break;
            default:
                name = null;
                break;
        }
    }
}
