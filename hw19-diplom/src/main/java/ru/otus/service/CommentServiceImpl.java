package ru.otus.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.annotation.Auditable;
import ru.otus.dto.UserDto;
import ru.otus.dto.taskComment.TaskCommentDto;
import ru.otus.entity.Task;
import ru.otus.entity.TaskComment;
import ru.otus.exception.CommonBusinessException;
import ru.otus.exception.EntityNotFoundException;
import ru.otus.exception.ServiceNotAvailableException;
import ru.otus.model.AuditActionEnum;
import ru.otus.model.AuditEntityTypeEnum;
import ru.otus.repository.TaskCommentRepository;
import ru.otus.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{

    private final TaskCommentRepository commentRepository;

    private final TaskRepository taskRepository;

    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackGetTaskComments")
    public List<TaskCommentDto> getTaskComments(Long taskId) {
        if (!taskRepository.existsByTaskId(taskId)) {
            throw new EntityNotFoundException("Task not found(taskId=%d)".formatted(taskId));
        }
        List<TaskComment> dataList = commentRepository.findByTask_TaskId(taskId);
        var resultList = dataList.stream()
                .map(TaskCommentDto::toDto)
                .toList();
        return resultList;
    }

    private List<TaskCommentDto> fallbackGetTaskComments(Long taskId,
                                                          Throwable ex) throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for fallbackGetTaskComments(taskId={})", taskId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @Auditable(action = AuditActionEnum.CREATED, entity = AuditEntityTypeEnum.TASK_COMMENT)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackCreateComment")
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

    private TaskCommentDto fallbackCreateComment(Long taskId,
                                                 String text,
                                                 Throwable ex) throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for fallbackGetTaskComments(taskId={}, text={}", taskId, text, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @PreAuthorize("@commentPolicy.isOwner(#commentId) or hasRole('ADMIN')")
    @Auditable(action = AuditActionEnum.DELETED, entity = AuditEntityTypeEnum.TASK_COMMENT, idFieldName = "commentId")
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackDeleteComment")
    public void deleteComment(Long commentId) {
        var isExists = commentRepository.existsByCommentId(commentId);
        if (!isExists) {
            throw new EntityNotFoundException("Task comment not found(commentId=%d)".formatted(commentId));
        }
        commentRepository.deleteById(commentId);
    }

    private void fallbackDeleteComment(Long commentId,
                                       Throwable ex) throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for deleteComment(commentId={}", commentId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackDeleteTaskComments")
    @Auditable(action = AuditActionEnum.DELETED, entity = AuditEntityTypeEnum.TASK_COMMENT)
    public List<TaskCommentDto> deleteTaskComments(Long taskId) {
        List<TaskComment> comments = commentRepository.deleteCommentsByTaskAndGet(taskId);
        return TaskCommentDto.toDtoList(comments);
    }

    private List<TaskCommentDto> fallbackDeleteTaskComments(Long taskId,
                                            Throwable ex) throws ServiceNotAvailableException {
        log.error("Fallback triggered for deleteTaskComments(taskId={}", taskId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

}