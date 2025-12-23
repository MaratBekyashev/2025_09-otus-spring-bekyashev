package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;

public interface BookService {
    Mono<BookDto> findById(Long bookId);

    Flux<BookDto> findAll();

    Mono<BookDto> insertBook(BookDto book);

    Mono<BookDto> updateBook(BookDto book);

    Mono<Void> deleteById(Long id);
}
