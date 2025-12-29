package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    public Mono<CommentDto> findById(Long id) {
        var comment = commentRepository.findById(id);
        return comment.map(CommentDto::new);
    }

    @Override
    public Flux<CommentDto> findAllCommentsByBookId(Long bookId) {
        var dataList = commentRepository.findAllByBookId(bookId);
        var resultList = dataList.map(CommentDto::toDto);
        return resultList;
    }

    @Override
    public Mono<CommentDto> insert(CommentDto commentDto) {
        var result = bookRepository
                .findById(commentDto.getBookId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s is not found".formatted(commentDto.getBookId()))))
                .flatMap(book -> commentRepository.save(new Comment(null, commentDto.getContent(), book.getId())))
                .map(CommentDto::new);

        return result;
    }

    @Override
    public Mono<CommentDto> update(CommentDto commentDto) {
        var result = Mono.zip(
                bookRepository
                        .findById(commentDto.getBookId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s is not found".formatted(commentDto.getBookId())))),
                commentRepository.findById(commentDto.getId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment with id %s is not found".formatted(commentDto.getId()))))
        ).flatMap(data -> {
            Book book = data.getT1();
            String changedComment = commentDto.getContent();

            return commentRepository.save(new Comment(commentDto.getId(), changedComment, book.getId()));
        }).map(CommentDto::new);
        return result;
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return commentRepository.deleteById(id);
    }

}
