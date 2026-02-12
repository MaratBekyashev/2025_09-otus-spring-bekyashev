package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
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

    @Override
    @Transactional(readOnly = true)
    public CommentDto findById(Long id) {
        var comment = commentRepository.findById(id).get();
        return CommentDto.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAllByBookId(Long bookId) {
        var dataList = commentRepository.findAllByBookId(bookId);
        var resultList = CommentDto.toDtoList(dataList);
        return resultList;
    }

    @Override
    public CommentDto insert(CommentDto commentDto) {
        Comment comment = save(null, commentDto.getContent(), commentDto.getBookId());
        return CommentDto.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto update(CommentDto commentDto) {
        var comment = save(commentDto.getId(), commentDto.getContent(), commentDto.getBookId());
        return CommentDto.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    private Comment save(Long id, String content, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment(id, content, book);
        var savedComment = commentRepository.save(comment);
        return savedComment;
    }
}
