package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.entity.User;
import ru.otus.repository.TaskRepository;
import ru.otus.security.CustomUserDetails;

import java.util.Optional;

@Service("taskSecurityService")
@RequiredArgsConstructor
public class TaskSecurityService {

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