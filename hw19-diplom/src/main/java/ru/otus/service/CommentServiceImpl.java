package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.annotation.Auditable;
import ru.otus.dto.UserDto;
import ru.otus.dto.taskComment.TaskCommentDto;
import ru.otus.entity.Task;
import ru.otus.entity.TaskComment;
import ru.otus.exception.EntityNotFoundException;
import ru.otus.model.AuditActionEnum;
import ru.otus.model.AuditEntityTypeEnum;
import ru.otus.repository.TaskCommentRepository;
import ru.otus.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;

    private final AuthService authService;

    @Transactional(readOnly = true)
    public List<TaskCommentDto> getTaskComments(Long taskId) {
        var dataList = commentRepository.findByTask_TaskId(taskId);
        var resultList = dataList.stream()
                .map(TaskCommentDto::toDto)
                .toList();
        return resultList;
    }

    @Transactional
    @Auditable(action = AuditActionEnum.CREATED, entity = AuditEntityTypeEnum.TASK_COMMENT, idFieldName = "commentId")
    public TaskCommentDto createComment(Long taskId, String text) {
        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found(taskId=%d)".formatted(taskId)));
        var author = UserDto.toDomain(authService.getCurrentUser());
        TaskComment comment = TaskComment.builder()
                .commentId(null)
                .comment(text)
                .task(task)
                .author(author)
                .createDate(LocalDateTime.now())
                .build();

        TaskComment saved = commentRepository.save(comment);

        return TaskCommentDto.toDto(saved);
    }


    @Transactional
    @PreAuthorize("@commentSecurityService.isOwner(#commentId) or hasRole('ADMIN')")
    @Auditable(action = AuditActionEnum.DELETED, entity = AuditEntityTypeEnum.TASK_COMMENT, idFieldName = "commentId")
    public void deleteComment(Long commentId) {
        var isExists = commentRepository.existsByCommentId(commentId);
        if (!isExists) {
            throw new EntityNotFoundException("Task comment not found(commentId=%d)".formatted(commentId));
        }
        commentRepository.deleteById(commentId);
    }
}