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
import ru.otus.dto.project.ProjectDto;
import ru.otus.dto.project.ProjectMemberDto;
import ru.otus.model.projects.CreateProjectRequest;
import ru.otus.model.projects.UpdateProjectRequest;
import ru.otus.security.TestSecurityConfig;
import ru.otus.service.ProjectService;
import ru.otus.util.JwtUtil;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ProjectController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectDto buildProject() {
        return ProjectDto.builder()
                .projectId(1L)
                .name("Test project")
                .description("desc")
                .createDate(LocalDateTime.now())
                .build();
    }

    @DisplayName("Список всех проектов")
    @Test
    void findAllProjects_shouldReturnList() throws Exception {
        Mockito.when(projectService.findAllProjects())
                .thenReturn(List.of(buildProject()));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectId").value(1));
    }

    @DisplayName("Поиск проекта по Id")
    @Test
    void findProjectById_shouldReturnProject() throws Exception {
        Mockito.when(projectService.findProject(1L))
                .thenReturn(buildProject());

        mockMvc.perform(get("/api/projects/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1));
    }

    @DisplayName("Создание проекта")
    @Test
    void createProject_shouldReturnCreatedProject() throws Exception {
        var request = new CreateProjectRequest("Test project", "desc");

        Mockito.when(projectService.createProject(any()))
                .thenReturn(buildProject());

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1));

        Mockito.verify(projectService).createProject(any());
    }

    @DisplayName("Тест валидации данных при создании проекта")
    @Test
    void createProject_shouldFail_whenNameIsNull() throws Exception {
        var request = new CreateProjectRequest(null, "desc");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("name: Имя проекта не может быть пустым;"));;
    }

    @DisplayName("Изменение проекта")
    @Test
    void editProject_shouldReturnUpdatedProject() throws Exception {
        var request = new UpdateProjectRequest("Updated", "desc");

        Mockito.when(projectService.editProject(any()))
                .thenReturn(buildProject());

        mockMvc.perform(put("/api/projects/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1));
    }

    @DisplayName("Удаление проекта")
    @Test
    void deleteProject_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", 1))
                .andExpect(status().isOk());

        Mockito.verify(projectService).deleteProject(1L);
    }



    @DisplayName("Найти всех участников заданного проекта")
    @Test
    void findAllProjectMembers_shouldReturnList() throws Exception {
        Mockito.when(projectService.findAllProjectMembers(1L))
                .thenReturn(List.of(new ProjectMemberDto()));

        mockMvc.perform(get("/api/projects/{id}/members", 1))
                .andExpect(status().isOk());
    }

    @DisplayName("Найти заданного участника заданного проекта")
    @Test
    void findProjectMember_shouldReturnMember() throws Exception {
        Mockito.when(projectService.findProjectMember(1L, 2L))
                .thenReturn(new ProjectMemberDto());

        mockMvc.perform(get("/api/projects/{projectId}/members/{memberId}", 1, 2))
                .andExpect(status().isOk());
    }

    @DisplayName("Добавление участника проекта")
    @Test
    void addProjectMember_shouldReturnCreatedMember() throws Exception {
        var request = new ProjectMemberDto();

        Mockito.when(projectService.addProjectMember(eq(1L), any()))
                .thenReturn(new ProjectMemberDto());

        mockMvc.perform(post("/api/projects/{projectId}/members", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @DisplayName("Изменение участника проекта")
    @Test
    void editProjectMember_shouldReturnUpdatedMember() throws Exception {
        var request = new ProjectMemberDto();

        Mockito.when(projectService.editProjectMember(eq(1L), any()))
                .thenReturn(new ProjectMemberDto());

        mockMvc.perform(put("/api/projects/{projectId}/members", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @DisplayName("Удаление участника проекта")
    @Test
    void deleteProjectMember_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/projects/{projectId}/members/{userId}", 1, 2))
                .andExpect(status().isOk());

        Mockito.verify(projectService).deleteProjectMember(1L, 2L);
    }
}