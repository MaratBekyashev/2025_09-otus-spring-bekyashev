package ru.otus.hw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;

@Component
@RequiredArgsConstructor
public class BookPolicy {

    private final BookRepository bookRepository;

    public boolean isOwner(Long bookId, Authentication authentication) {
        boolean result = false;
        if (bookId != null) {
            Book book = bookRepository.findById(bookId).orElseThrow();
            String username = authentication.getName();
            String createUser = book.getCreateUser();
            result = createUser.equals(username);

            return result;
        }

        return false;
    }
}
