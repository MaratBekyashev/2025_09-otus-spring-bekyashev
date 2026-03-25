package ru.otus.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.repository.TaskCommentRepository;

@Service("commentPolicy")
@RequiredArgsConstructor
public class CommentPolicy {

    private final TaskCommentRepository commentRepository;

    public boolean isOwner(Long commentId) {
        String username = getPrincipal().getUsername();
        var result = commentRepository
                .findById(commentId)
                .map(cmnt -> {
                    String authorLogin = cmnt.getAuthor().getLogin();
                    return username.equals(authorLogin);
                })
                .orElse(false);
        return result;
    }

    private CustomUserDetails getPrincipal() {
        var result = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return result;
    }
}