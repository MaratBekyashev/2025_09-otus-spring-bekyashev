package ru.otus.service;

import ru.otus.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto findById(Long id);

    List<BookDto> findAll();

    BookDto insert(BookDto book);

    BookDto update(BookDto book);

    void deleteById(Long id);
}
