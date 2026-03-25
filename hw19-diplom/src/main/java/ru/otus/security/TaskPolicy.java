package ru.otus.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.repository.TaskRepository;

@Service("taskPolicy")
@RequiredArgsConstructor
public class TaskPolicy {

    private final TaskRepository taskRepository;

    public boolean isUserTaskOwner(Long taskId) {
        String username = getPrincipal().getUsername();
        var result = taskRepository
                .findById(taskId)
                .map(task -> {
                    String assigneeUser = task.getAssignee() != null? task.getAssignee().getLogin():"";
                    return username.equals(assigneeUser) ||
                           username.equals(task.getCreateUser()) ;
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