package ru.otus.dto.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.entity.Task;
import ru.otus.model.IdentifableEntity;
import ru.otus.model.task.TaskAssigneeUserSimple;
import ru.otus.model.task.TaskPriorityEnum;
import ru.otus.model.task.TaskProjectSimple;
import ru.otus.model.task.TaskStatusEnum;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDto implements IdentifableEntity {

    private Long taskId;

    private String title;

    private String description;

    private TaskStatusEnum status;

    private TaskPriorityEnum priority;

    private TaskProjectSimple project;

    private TaskAssigneeUserSimple assignee;

    private LocalDateTime createDate;

    private LocalDateTime dueDate;

    public static TaskResponseDto toDto (Task task) {
        var project = task.getProject();
        var assignee = task.getAssignee();
        var result = TaskResponseDto.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .project(project != null? new TaskProjectSimple(project.getProjectId(), project.getName()):null)
                .assignee(assignee != null? new TaskAssigneeUserSimple(assignee.getUserId(), assignee.getUserName()):null)
                .dueDate(task.getDueDate())
                .createDate(task.getCreateDate())
                .build();

        return result;
    }

    public static List<TaskResponseDto> toDtoList (List<Task> tasks) {
        return tasks.stream()
                .map(TaskResponseDto::toDto)
                .toList();
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return this.taskId;
    }
}
