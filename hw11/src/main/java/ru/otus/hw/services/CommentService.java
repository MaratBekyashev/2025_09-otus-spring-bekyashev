package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {

    Mono<CommentDto> findById(Long id);

    Flux<CommentDto> findAllCommentsByBookId(Long bookId);

    Mono<CommentDto> insert(CommentDto commentDto);

    Mono<CommentDto> update(CommentDto commentDto);

    Mono<Void> deleteById(Long id);
}
