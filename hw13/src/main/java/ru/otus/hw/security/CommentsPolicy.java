package ru.otus.hw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.CommentRepository;

@Component
@RequiredArgsConstructor
public class CommentsPolicy {

    private final CommentRepository commentRepository;

    public boolean isOwner(Long commentId, Authentication authentication) {
        if (commentId != null) {
            Comment comment = commentRepository.findById(commentId).orElseThrow();
            String username = authentication.getName();
            String createUserName = comment.getCreateUser();

            boolean result = createUserName.equals(username);
            return result;
        }

        return false;
    }
}
