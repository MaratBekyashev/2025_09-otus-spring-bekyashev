package ru.otus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.dto.task.TaskResponseDto;
import ru.otus.model.task.CreateTaskRequest;
import ru.otus.model.task.TaskPriorityEnum;
import ru.otus.model.task.TaskStatusEnum;
import ru.otus.model.task.UpdateTaskRequest;
import ru.otus.security.TestSecurityConfig;
import ru.otus.service.TaskService;
import ru.otus.util.JwtUtil;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {TaskController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    JwtUtil jwtUtil;

    private TaskResponseDto buildResponse() {
        return TaskResponseDto.builder()
                .taskId(1L)
                .title("Test task")
                .description("desc")
                .status(TaskStatusEnum.TODO)
                .priority(TaskPriorityEnum.MEDIUM)
                .createDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    @DisplayName("Поиск задач по критериям")
    @Test
    void searchTasks_shouldReturnFilteredTasks() throws Exception {
        var response = List.of(
                TaskResponseDto.builder()
                        .taskId(1L)
                        .title("Test task")
                        .build()
        );

        Mockito.when(taskService.taskSearch(any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/tasks/search")
                        .param("projectId", "1")
                        .param("assigneeId", "2")
                        .param("status", "TODO", "IN_PROGRESS")
                        .param("priority", "HIGH")
                        .param("title", "Test")
                        .param("dueDateFrom", "2025-01-01")
                        .param("dueDateTo", "2025-12-31")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value(1));

        Mockito.verify(taskService).taskSearch(any());
    }

    @DisplayName("Поиск задачи по ID")
    @Test
    void getTask_shouldReturnTask() throws Exception {
        var response = buildResponse();

        Mockito.when(taskService.getTask(1L)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/{taskId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1))
                .andExpect(jsonPath("$.title").value("Test task"));
    }

    @DisplayName("Создание задачи")
    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        var request = new CreateTaskRequest(
                "Test task",
                "desc",
                TaskPriorityEnum.HIGH,
                10L,
                LocalDateTime.now().plusDays(1)
        );

        var response = buildResponse();

        Mockito.when(taskService.createTask(
                anyLong(), anyString(), anyString(),
                any(), anyLong(), any()
        )).thenReturn(response);

        mockMvc.perform(post("/api/tasks/projects/{projectId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1));
    }

    @DisplayName("Проверка валидации параметров при создании задачи")
    @Test
    void createTask_shouldFail_whenTitleIsBlank() throws Exception {
        var request = new CreateTaskRequest(
                "",
                "desc",
                TaskPriorityEnum.HIGH,
                1L,
                null
        );

        mockMvc.perform(post("/api/tasks/projects/{projectId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Обновление задачи")
    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        var request = new UpdateTaskRequest(
                "Updated",
                "desc",
                TaskStatusEnum.IN_PROGRESS,
                TaskPriorityEnum.MEDIUM,
                5L,
                LocalDateTime.now().plusDays(2)
        );

        var response = buildResponse();

        Mockito.when(taskService.updateTask(
                anyLong(), any(), any(), any(), any(), any(), any()
        )).thenReturn(response);

        mockMvc.perform(put("/api/tasks/{taskId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1));
    }

    @DisplayName("Удаление задачи")
    @Test
    void deleteTask_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/tasks/{taskId}", 1))
                .andExpect(status().isOk());

        Mockito.verify(taskService).deleteTask(1L);
    }

    @DisplayName("Список задач проекта")
    @Test
    void getTasksByProject_shouldReturnList() throws Exception {
        var response = List.of(buildResponse());

        Mockito.when(taskService.getProjectTasks(1L)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/projects/{projectId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value(1));
    }
}