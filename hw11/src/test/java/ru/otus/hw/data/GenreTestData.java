package ru.otus.hw.data;

import ru.otus.hw.models.Genre;
import java.util.List;

public class GenreTestData {

    public static List<Genre> getDbGenres() {
        return List.of(
                new Genre(1L, "genre_1"),
                new Genre(2L, "genre_2"),
                new Genre(3L, "genre_3"));
    }
}
