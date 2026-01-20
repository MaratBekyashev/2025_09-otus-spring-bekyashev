package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        return new BookDto(bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return BookDto.toDtoList(bookRepository.findAll());
    }

    @Override
    @Transactional
    public BookDto insert(BookInsertUpdateDto bookIn) {
        Book createdBook = save(null, bookIn.getTitle(), bookIn.getAuthorId(), bookIn.getGenreId());
        return new BookDto(createdBook);
    }

    @Override
    @Transactional
    @PreAuthorize("@bookPolicy.isOwner(#book.id, authentication) || hasRole('ADMIN')")
    public BookDto update(BookInsertUpdateDto book) {
        Book updatedBook = save(book.getId(), book.getTitle(), book.getAuthorId(), book.getGenreId());
        return new BookDto(updatedBook);
    }

    @Override
    @Transactional
    @PreAuthorize("@bookPolicy.isOwner(#bookId, authentication) || hasRole('ADMIN')")
    public void deleteById(Long bookId) {
        bookRepository.deleteById(bookId);
    }

    private Book save(Long id, String title, Long authorId, Long genreId) {
        var author = authorRepository
                .findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genre = genreRepository
                .findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreId)));
        var book = new Book(id, title, author, genre, userService.getCurrentUsername());
        return bookRepository.save(book);
    }
}
