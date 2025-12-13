package ru.otus.hw.services;

import ru.otus.hw.dto.CommentInsertUpdateDto;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<Comment> findById(Long id);

    List<Comment> findAllByBookId(Long bookId);

    Comment insert(CommentInsertUpdateDto commentInsertUpdateDto);

    Comment update(CommentInsertUpdateDto commentInsertUpdateDto);

    void deleteById(Long id);
}
