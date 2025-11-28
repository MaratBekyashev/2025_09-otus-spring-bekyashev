package ru.otus.hw.services;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> findById(Long id);

    List<Book> findAll();

    Book insert(String title, Long authorId, Long genreId);

    Book update(Long id, String title, Long authorId, Long genreId);

    void deleteById(Long id);
}
