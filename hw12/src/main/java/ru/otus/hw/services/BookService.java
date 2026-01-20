package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    BookDto findById(Long id);

    List<BookDto> findAll();

    BookDto insert(BookInsertUpdateDto book);

    BookDto update(BookInsertUpdateDto book);

    void deleteById(Long id);
}
