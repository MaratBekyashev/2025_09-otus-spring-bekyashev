package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.UpdateBookDto;
import ru.otus.hw.services.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping("/api/books")
    public ResponseEntity<Flux<BookDto>> getListBooksPage() {
        var bookList = bookService.findAll();
        return ResponseEntity.ok(bookList);
    }

    @GetMapping("/api/books/{id}")
    public ResponseEntity<Mono<BookDto>> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PutMapping("/api/books/{id}")
    public ResponseEntity<Mono<BookDto>> updateBook(@PathVariable Long id,
                                              @RequestBody UpdateBookDto bookDto) {
        BookDto book = BookDto.toDto(id, bookDto.getTitle(),bookDto.getAuthor(), bookDto.getGenre());
        var result = bookService.updateBook(book);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/books")
    public ResponseEntity<Mono<BookDto>> createBook(@RequestBody BookDto bookDto) {
        var result = bookService.insertBook(bookDto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/books/{id}")
    public Mono<Void> deleteBook(@PathVariable Long id) {
        return bookService.deleteById(id);
    }
}
