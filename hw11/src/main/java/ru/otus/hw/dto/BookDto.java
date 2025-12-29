package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    private String title;

    private Author author;

    private Genre genre;

    public BookDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = new Author(book.getAuthorId(), "");
        this.genre = new Genre(book.getGenreId(), "");
    }

    public BookDto(Book book, Author author, Genre genre) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = author;
        this.genre = genre;
    }

    public BookDto(Long bookId, String bookTitle, Long authorId, String authorName, Long genreId, String genreName) {
        this.id = bookId;
        this.title = bookTitle;
        this.author = new Author(authorId, authorName);
        this.genre = new Genre(genreId, genreName);
    }

    public static BookDto toDto(Long bookId,
                                String title,
                                Author author,
                                Genre genre){
        return new BookDto(bookId, title, author, genre);
    }

    public static List<BookDto> toDtoList(List<Book> books) {
        return books.stream()
                .map(BookDto::new)
                .toList();
    }
}
