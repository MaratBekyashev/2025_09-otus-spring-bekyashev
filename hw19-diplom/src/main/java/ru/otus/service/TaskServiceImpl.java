package ru.otus.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.annotation.Auditable;
import ru.otus.dto.task.TaskResponseDto;
import ru.otus.entity.Project;
import ru.otus.entity.Task;
import ru.otus.entity.User;
import ru.otus.exception.CommonBusinessException;
import ru.otus.exception.EntityNotFoundException;
import ru.otus.exception.ServiceNotAvailableException;
import ru.otus.model.AuditActionEnum;
import ru.otus.model.AuditEntityTypeEnum;
import ru.otus.model.task.TaskPriorityEnum;
import ru.otus.model.task.TaskSearchFilter;
import ru.otus.model.task.TaskSearchSpecification;
import ru.otus.model.task.TaskStatusEnum;
import ru.otus.repository.ProjectRepository;
import ru.otus.repository.TaskRepository;
import ru.otus.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    private final CommentService commentService;

    private final AuthService authService;

    private final Counter tasksCreated;

    public TaskServiceImpl(TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           UserRepository userRepository,
                           AuthService authService,
                           CommentService commentService,
                           MeterRegistry registry) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.commentService = commentService;
        this.tasksCreated = Counter
                .builder("tasks.currentCount")
                .description("Quantity of active tasks")
                .register(registry);
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackTaskSearch")
    public List<TaskResponseDto> taskSearch(TaskSearchFilter filter) {
        Specification<Task> spec = TaskSearchSpecification.build(filter);
        List<Task> dataList = taskRepository.findAll(spec);
        return TaskResponseDto.toDtoList(dataList);
    }

    private List<TaskResponseDto> fallbackTaskSearch(TaskSearchFilter filter,
                                                     Throwable ex) throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for taskSearch(filter={})", filter, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackGetProjectTasks")
    public List<TaskResponseDto> getProjectTasks(Long projectId) {
        List<Task> dataList = taskRepository.findByProject_ProjectId(projectId);
        var resultList = TaskResponseDto.toDtoList(dataList);
        return resultList;
    }

    private List<TaskResponseDto> fallbackGetProjectTasks(Long projectId,
                                                         Throwable ex) throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for getProjectTasks(projectId={})", projectId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackGetTask")
    public TaskResponseDto getTask(Long taskId) {
        var task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found(taskId=%s)".formatted(taskId)));
        var result = TaskResponseDto.toDto(task);
        return result;
    }

    private List<TaskResponseDto> fallbackGetTask(Long taskId,
                                                  Throwable ex) throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for getTask(taskId={})", taskId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Transactional
    @Override
    @PreAuthorize("@projectPolicy.isUserProjectMember(#projectId) or hasRole('ADMIN')")
    @Auditable(action = AuditActionEnum.CREATED, entity = AuditEntityTypeEnum.TASK)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackCreateTask")
    public TaskResponseDto createTask(Long projectId,
                                      String title,
                                      String description,
                                      TaskPriorityEnum priority,
                                      Long assigneeId,
                                      LocalDateTime dueDate) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found(projectId=%d)".formatted(projectId)));

        User assignee = null;

        if (assigneeId != null) {
            assignee = userRepository
                    .findById(assigneeId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found(userId=%d)".formatted(assigneeId)));
        }

        Task task = Task.builder()
                .taskId(null)
                .title(title)
                .description(description)
                .priority(priority)
                .status(TaskStatusEnum.TODO)
                .project(project)
                .assignee(assignee)
                .createUser(authService.getCurrentUser().getUserName())
                .createDate(LocalDateTime.now())
                .dueDate(dueDate)
                .build();

        var saved = taskRepository.save(task);
        this.tasksCreated.increment(1);
        var result = TaskResponseDto.toDto(saved);
        return result;
    }

    private List<TaskResponseDto> fallbackCreateTask(Long projectId,
                                                     String title,
                                                     String description,
                                                     TaskPriorityEnum priority,
                                                     Long assigneeId,
                                                     LocalDateTime dueDate,
                                                     Throwable ex) throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for createTask(projectId={}, title={}, description={}, priority={})",
                projectId, title, description, priority, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @PreAuthorize("@taskPolicy.isUserTaskOwner(#taskId) or hasRole('ADMIN')")
    @Auditable(action = AuditActionEnum.EDITED, entity = AuditEntityTypeEnum.TASK)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackUpdateTask")
    public TaskResponseDto updateTask(Long taskId,
                                      String title,
                                      String description,
                                      TaskStatusEnum status,
                                      TaskPriorityEnum priority,
                                      Long assigneeId,
                                      LocalDateTime dueDate) {
        var task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(("Task not found(taskId=%d)".formatted(taskId))));

        if (!Optional.ofNullable(title).orElse("").isEmpty()) {
            task.setTitle(title);
        }
        if (!Optional.ofNullable(description).orElse("").isEmpty()) {
            task.setDescription(description);
        }
        if (status != null) {
            task.setStatus(status);
        }
        if (priority != null) {
            task.setPriority(priority);
        }
        if (dueDate != null) {
            task.setDueDate(dueDate);
        }
        if (assigneeId != null) {
            User assignee = userRepository
                    .findById(assigneeId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found(userId=%d)".formatted(assigneeId)));
            task.setAssignee(assignee);
        }
        taskRepository.save(task);
        return TaskResponseDto.toDto(task);
    }

    private List<TaskResponseDto> fallbackUpdateTask(Long taskId,
                                                     String title,
                                                     String description,
                                                     TaskStatusEnum status,
                                                     TaskPriorityEnum priority,
                                                     Long assigneeId,
                                                     LocalDateTime dueDate,
                                                     Throwable ex) throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for updateTask(taskId={}, title={}, description={}, status={}, priority={}"+
                "assigneeId={}, dueDate={})",
                taskId, title, description, status, priority, assigneeId, dueDate, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @PreAuthorize("@taskPolicy.isUserTaskOwner(#taskId) or hasRole('ADMIN')")
    @Auditable(action = AuditActionEnum.DELETED, entity = AuditEntityTypeEnum.TASK, idFieldName = "taskId")
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackDeleteTask")
    public void deleteTask(Long taskId) {
        commentService.deleteTaskComments(taskId);
        taskRepository.deleteById(taskId);
        this.tasksCreated.increment(-1);
    }

    private List<TaskResponseDto> fallbackDeleteTask(Long taskId,
                                                     Throwable ex) throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for updateTask(taskId={})", taskId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

}