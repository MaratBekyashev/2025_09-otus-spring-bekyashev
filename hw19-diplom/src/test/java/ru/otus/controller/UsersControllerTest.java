package ru.otus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.dto.UserDto;
import ru.otus.dto.user.CreateUserRequestDto;
import ru.otus.dto.user.UpdateUserRequestDto;
import ru.otus.security.TestSecurityConfig;
import ru.otus.service.UserService;
import ru.otus.util.JwtUtil;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto buildUser() {
        UserDto user = new UserDto();
        user.setUserId(1L);
        user.setLogin("test");
        user.setUserName("Test User");
        user.setEmail("test@mail.com");
        user.setRoles(Set.of("USER"));
        return user;
    }

    @DisplayName("Поиск пользователя по Id")
    @Test
    void getUser_shouldReturnUser() throws Exception {
        Mockito.when(userService.findUserById(1L))
                .thenReturn(buildUser());

        mockMvc.perform(get("/api/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("test"));
    }

    @DisplayName("Поиск пользователей по критериям")
    @Test
    void search_shouldReturnFilteredUsers() throws Exception {
        Mockito.when(userService.search(any()))
                .thenReturn(List.of(buildUser()));

        mockMvc.perform(get("/api/users/search")
                        .param("login", "test")
                        .param("email", "test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("test"));

        Mockito.verify(userService).search(any());
    }

    @DisplayName("Создание пользователя")
    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        var request = new CreateUserRequestDto();
        request.setLogin("test");
        request.setPassword("123");
        request.setFullUserName("Test User");

        Mockito.when(userService.createUser(any()))
                .thenReturn(buildUser());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @DisplayName("Валидация параметров при создании пользователя")
    @Test
    void createUser_shouldFail_whenLoginIsBlank() throws Exception {
        var request = new CreateUserRequestDto();
        request.setLogin("");
        request.setPassword("123");
        request.setFullUserName("Test User");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("login: Логин пользователя должен быть указан;"));
    }

    @DisplayName("Изменение пользователя")
    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        var request = new UpdateUserRequestDto();
        request.setLogin("updated");

        Mockito.when(userService.updateUser(eq(1L), any()))
                .thenReturn(buildUser());

        mockMvc.perform(put("/api/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @DisplayName("Удаление пользователя")
    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 1))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUserById(1L);
    }
}