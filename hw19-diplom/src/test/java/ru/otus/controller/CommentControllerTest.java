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
import ru.otus.dto.taskComment.CreateCommentRequest;
import ru.otus.dto.taskComment.TaskCommentDto;
import ru.otus.security.TestSecurityConfig;
import ru.otus.service.CommentService;
import ru.otus.util.JwtUtil;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskCommentDto buildComment(Long id) {
        return TaskCommentDto.builder()
                .commentId(id)
                .taskId(1L)
                .text("Test comment")
                .authorId(1L)
                .authorName("Author")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @DisplayName("Получение комментариев для заданной задачи")
    @Test
    void getComments_shouldReturnList() throws Exception {
        Mockito.when(commentService.getTaskComments(1L))
                .thenReturn(List.of(buildComment(1L)));

        mockMvc.perform(get("/api/tasks/{taskId}/comments", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(1));
    }

    @DisplayName("Создание комментария")
    @Test
    void createComment_shouldReturnComment() throws Exception {
        var request = new CreateCommentRequest("New comment");

        Mockito.when(commentService.createComment(eq(1L), any()))
                .thenReturn(buildComment(2L));

        mockMvc.perform(post("/api/tasks/{taskId}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(2));
    }

    @DisplayName("Валидация параметров при создании комментария")
    @Test
    void createComment_shouldFail_whenTextBlank() throws Exception {
        var request = new CreateCommentRequest("");

        mockMvc.perform(post("/api/tasks/{taskId}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Удаление комментария по Id")
    @Test
    void deleteComment_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/tasks/{taskId}/comments/{commentId}", 1, 1))
                .andExpect(status().isOk());

        Mockito.verify(commentService).deleteComment(1L);
    }
}