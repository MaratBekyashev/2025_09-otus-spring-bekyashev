package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.task.TaskResponseDto;
import ru.otus.model.task.CreateTaskRequest;
import ru.otus.model.task.TaskPriorityEnum;
import ru.otus.model.task.TaskSearchFilter;
import ru.otus.model.task.TaskStatusEnum;
import ru.otus.model.task.UpdateTaskRequest;
import ru.otus.service.TaskService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponseDto>> searchTasks(@RequestParam(required = false) Long projectId,
                                                             @RequestParam(required = false) Long assigneeId,
                                                             @RequestParam(required = false) List<TaskStatusEnum> status,
                                                             @RequestParam(required = false) TaskPriorityEnum priority,
                                                             @RequestParam(required = false) String title,
                                                             @RequestParam(required = false) LocalDate dueDateFrom,
                                                             @RequestParam(required = false) LocalDate dueDateTo) {
        TaskSearchFilter filter = new TaskSearchFilter(projectId, assigneeId, status, priority, title, dueDateFrom, dueDateTo);
        var response = taskService.taskSearch(filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<TaskResponseDto>> getTasks(@PathVariable("projectId") Long projectId) {
        var response = taskService.getProjectTasks(projectId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}")
    public TaskResponseDto getTask(@PathVariable Long taskId) {
        return taskService.getTask(taskId);
    }

    @PostMapping("/projects/{projectId}")
    public ResponseEntity<TaskResponseDto> createTask(@PathVariable("projectId") Long projectId,
                                                     @RequestBody @Valid CreateTaskRequest request) {
        var response = taskService.createTask(
                projectId,
                request.title(),
                request.description(),
                request.priority(),
                request.assigneeId(),
                request.dueDate());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable("taskId") Long taskId,
                                      @RequestBody UpdateTaskRequest request) {
        var response = taskService.updateTask(
                taskId,
                request.title(),
                request.description(),
                request.status(),
                request.priority(),
                request.assigneeId(),
                request.dueDate());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }
}