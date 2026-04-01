package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackForFindAllAuthors")
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    private List<Author> fallbackForFindAllAuthors(Throwable ex) {
        log.error("DB unavailable, fallback triggered", ex);
        return Collections.emptyList();
    }
}
