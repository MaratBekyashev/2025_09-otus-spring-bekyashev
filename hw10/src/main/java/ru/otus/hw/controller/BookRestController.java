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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping("/api/library/book")
    public ResponseEntity<List<BookDto>> getListBooksPage() {
        var bookList = bookService.findAll();
        return ResponseEntity.ok(bookList);
    }

    @GetMapping("/api/library/book/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PutMapping("/api/library/book/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id,
                                              @RequestBody BookDto bookDto) {
        var result = bookService.update(bookDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/library/book")
    public ResponseEntity<BookDto> createBook(@RequestBody
                                              BookDto bookDto) {
        var result = bookService.insert(bookDto);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/library/book/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
