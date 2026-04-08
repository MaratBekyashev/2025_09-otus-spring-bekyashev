package ru.otus.service;

import ru.otus.dto.task.TaskResponseDto;
import ru.otus.model.task.TaskPriorityEnum;
import ru.otus.model.task.TaskSearchFilter;
import ru.otus.model.task.TaskStatusEnum;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {

    List<TaskResponseDto> taskSearch (TaskSearchFilter filter);

    List<TaskResponseDto> getProjectTasks(Long projectId);

    TaskResponseDto getTask(Long taskId) ;

    TaskResponseDto createTask(Long projectId,
                               String title,
                               String description,
                               TaskPriorityEnum priority,
                               Long assigneeId,
                               LocalDateTime dueDate);

    TaskResponseDto updateTask(Long taskId,
                               String title,
                               String description,
                               TaskStatusEnum status,
                               TaskPriorityEnum priority,
                               Long assigneeId,
                               LocalDateTime dueDate);

    void deleteTask(Long taskId);
}