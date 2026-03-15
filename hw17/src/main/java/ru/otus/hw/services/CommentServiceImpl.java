package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.exceptions.ServiceNotAvailableException;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackFindCommentById")
    public CommentDto findById(Long id) {
        Comment comment = commentRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));
        return CommentDto.toDto(comment);
    }

    private CommentDto fallbackFindCommentById(Long id, Throwable ex) throws ServiceNotAvailableException {
        log.error("Fallback triggered for findById({})", id, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackFindCommentByBookId")
    public List<CommentDto> findAllByBookId(Long bookId) {
        var dataList = commentRepository.findAllByBookId(bookId);
        var resultList = CommentDto.toDtoList(dataList);
        return resultList;
    }

    private List<CommentDto> fallbackFindCommentByBookId(Long bookId, Throwable ex) throws ServiceNotAvailableException {
        log.error("Fallback triggered for findAllByBookId(book.id={})", bookId, ex);

        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackForSaveComment")
    public CommentDto insert(CommentDto commentDto) {
        Comment comment = save(null, commentDto.getContent(), commentDto.getBookId());
        return CommentDto.toDto(comment);
    }

    @Override
    @Transactional
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackForSaveComment")
    public CommentDto update(CommentDto commentDto) {
        var comment = save(commentDto.getId(), commentDto.getContent(), commentDto.getBookId());
        return CommentDto.toDto(comment);
    }

    private CommentDto fallbackForSaveComment(CommentDto commentDto, Throwable ex) throws ServiceNotAvailableException {
        log.error("Fallback triggered for save comment (book.id={})", commentDto.getBookId(), ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackForDeleteComment")
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    private void fallbackForDeleteComment(Long id, Throwable ex) throws ServiceNotAvailableException {
        log.error("Fallback triggered for deleting comment (id={})", id, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    private Comment save(Long id, String content, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment(id, content, book);
        var savedComment = commentRepository.save(comment);
        return savedComment;
    }
}
