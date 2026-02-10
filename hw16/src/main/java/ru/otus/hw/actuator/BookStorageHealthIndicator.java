package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

@Component
@RequiredArgsConstructor
public class BookStorageHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    @Override
    public Health health() {
        try {
            long count = bookRepository.count();

            return Health.up()
                    .withDetail("books.count", count)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("error", "Book storage is not available")
                    .build();
        }
    }
}
