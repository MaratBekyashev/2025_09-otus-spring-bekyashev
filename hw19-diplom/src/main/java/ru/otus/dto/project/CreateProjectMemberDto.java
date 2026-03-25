package ru.otus.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.dto.UserDto;
import ru.otus.model.ProjectRoleEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectMemberDto {

    @NotBlank(message = "Пользователь должен быть указан при включении в проект")
    private UserDto user;

    private ProjectRoleEnum roleInProject;

}