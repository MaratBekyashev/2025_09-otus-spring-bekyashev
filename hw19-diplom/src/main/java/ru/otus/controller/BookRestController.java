package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.otus.dto.BookDto;
import ru.otus.service.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping("/api/books")
    public ResponseEntity<List<BookDto>> getListBooksPage() {
        var bookList = bookService.findAll();
        return ResponseEntity.ok(bookList);
    }

    @GetMapping("/api/books/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PutMapping("/api/books/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id,
                                              @RequestBody UpdateBookDto bookDto) {
        BookDto book = BookDto.toDto(id, bookDto.getTitle(),bookDto.getAuthor(), bookDto.getGenre());
        var result = bookService.update(book);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/books")
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        var result = bookService.insert(bookDto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/books/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
