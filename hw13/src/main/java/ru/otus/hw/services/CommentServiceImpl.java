package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentInsertUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllByBookId(Long bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Override
    @Transactional
    public Comment insert(CommentInsertUpdateDto commentInsertUpdateDto) {
        return save(null, commentInsertUpdateDto.getContent(),
                commentInsertUpdateDto.getBookId());
    }

    @Override
    @Transactional
    @PreAuthorize("@commentsPolicy.isOwner(#commentInsertUpdateDto.id, authentication) || hasRole('ADMIN')")
    public Comment update(CommentInsertUpdateDto commentInsertUpdateDto) {
        return save(commentInsertUpdateDto.getId(),
                    commentInsertUpdateDto.getContent(),
                    commentInsertUpdateDto.getBookId());
    }

    @Override
    @Transactional
    @PreAuthorize("@commentsPolicy.isOwner(#id, authentication) || hasRole('ADMIN')")
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    private Comment save(Long id, String content, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment(id, content, book, userService.getCurrentUsername());
        return commentRepository.save(comment);
    }
}
