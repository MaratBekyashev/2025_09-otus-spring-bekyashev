package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.model.userRole;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    private String title;

    private userRole author;

    private Genre genre;

    public BookDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.genre = book.getGenre();
    }

    public static BookDto toDto(Long bookId,
                                String title,
                                userRole author,
                                Genre genre){
        return new BookDto(bookId, title, author, genre);
    }

    public static List<BookDto> toDtoList(List<Book> books) {
        return books.stream()
                .map(BookDto::new)
                .toList();
    }
}
