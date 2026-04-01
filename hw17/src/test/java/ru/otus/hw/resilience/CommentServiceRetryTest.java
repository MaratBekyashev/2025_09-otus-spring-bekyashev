package ru.otus.hw.resilience;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.exceptions.ServiceNotAvailableException;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.services.CommentService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=never"
})
class CommentServiceRetryTest {

    @Autowired
    private CommentService commentService;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private BookRepository bookRepository;

    @Test
    void shouldRetryThreeTimesWhenRepositoryFails() {
        // given
        when(commentRepository.findById(anyLong()))
                .thenThrow(new RuntimeException("DB down"));

        // when + then
        assertThrows(ServiceNotAvailableException.class,
                () -> commentService.findById(1L));

        // verify
        verify(commentRepository, times(3)).findById(1L);
    }
}
