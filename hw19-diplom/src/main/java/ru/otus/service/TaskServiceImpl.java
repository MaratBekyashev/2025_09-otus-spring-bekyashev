package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.annotation.Auditable;
import ru.otus.dto.task.TaskResponseDto;
import ru.otus.entity.Project;
import ru.otus.entity.Task;
import ru.otus.entity.User;
import ru.otus.exception.EntityNotFoundException;
import ru.otus.model.AuditActionEnum;
import ru.otus.model.AuditEntityTypeEnum;
import ru.otus.model.task.TaskPriorityEnum;
import ru.otus.model.task.TaskSearchFilter;
import ru.otus.model.task.TaskSpecification;
import ru.otus.model.task.TaskStatusEnum;
import ru.otus.repository.ProjectRepository;
import ru.otus.repository.TaskRepository;
import ru.otus.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private final AuthService authService;

    @Override
    public List<TaskResponseDto> taskSearch(TaskSearchFilter filter) {
        Specification<Task> spec = TaskSpecification.build(filter);
        List<Task> dataList = taskRepository.findAll(spec);
        return TaskResponseDto.toDtoList(dataList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TaskResponseDto> getProjectTasks(Long projectId) {
        List<Task> dataList = taskRepository.findByProject_ProjectId(projectId);
        var resultList = TaskResponseDto.toDtoList(dataList);
        return resultList;
    }

    @Transactional(readOnly = true)
    public TaskResponseDto getTask(Long taskId) {
        var task = taskRepository
                .findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found(taskId=%s)".formatted(taskId)));
        var result = TaskResponseDto.toDto(task);
        return result;
    }

    @Transactional
    @Override
    @PreAuthorize("@projectSecurityService.isUserProjectMember(#projectId)")
    @Auditable(action = AuditActionEnum.CREATED, entity = AuditEntityTypeEnum.TASK, idFieldName = "taskId")
    public TaskResponseDto createTask(Long projectId,
                                      String title,
                                      String description,
                                      TaskPriorityEnum priority,
                                      Long assigneeId,
                                      LocalDateTime dueDate) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User assignee = null;

        if (assigneeId != null) {
            assignee = userRepository
                    .findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
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

        taskRepository.save(task);

        var result = TaskResponseDto.toDto(task);
        return result;
    }

    @Transactional
    @Override
    @PreAuthorize("@taskSecurityService.isUserTaskOwner(#taskId) or hasRole('ADMIN')")
    @Auditable(action = AuditActionEnum.EDITED, entity = AuditEntityTypeEnum.TASK, idFieldName = "taskId")
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

        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);

        if (assigneeId != null) {
            User assignee = userRepository
                    .findById(assigneeId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found(userId=%d)".formatted(assigneeId)));
            task.setAssignee(assignee);
        }

        taskRepository.save(task);

        return TaskResponseDto.toDto(task);
    }

    @Transactional
    @Override
    @PreAuthorize("@taskSecurityService.isUserTaskOwner(#taskId) or hasRole('ADMIN')")
    @Auditable(action = AuditActionEnum.DELETED, entity = AuditEntityTypeEnum.TASK, idFieldName = "taskId")
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}