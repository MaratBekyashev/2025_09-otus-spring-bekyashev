package ru.otus.hw.data;

import ru.otus.hw.models.Author;
import java.util.List;

public class AuthorTestData {

    public static List<Author> getDbAuthors() {
        return List.of(new Author(1L, "author_1"),
                new Author(2L, "author_2"),
                new Author(3L, "author_3"));
    }
}
