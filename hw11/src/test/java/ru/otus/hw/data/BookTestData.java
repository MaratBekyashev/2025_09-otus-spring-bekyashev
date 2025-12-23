package ru.otus.hw.data;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import java.util.List;

public class BookTestData {

    public static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {

        return List.of(
                new Book(1L, "title_1", dbAuthors.get(0).getId(), dbGenres.get(0).getId()),
                new Book(2L, "title_2", dbAuthors.get(1).getId(), dbGenres.get(1).getId()),
                new Book(3L, "title_3", dbAuthors.get(2).getId(), dbGenres.get(2).getId())
        );
    }

    public static List<Book> getDbBooks() {
        var dbAuthors = AuthorTestData.getDbAuthors();
        var dbGenres = GenreTestData.getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }

    public static Book getNewBook() {
        return new Book(4L, "title_new_book", AuthorTestData.getDbAuthors().get(0).getId(), GenreTestData.getDbGenres().get(0).getId());
    }

    public static Book getChangeBook() {
        return new Book(1L, "title_change_book", AuthorTestData.getDbAuthors().get(1).getId(), GenreTestData.getDbGenres().get(1).getId());
    }
}
