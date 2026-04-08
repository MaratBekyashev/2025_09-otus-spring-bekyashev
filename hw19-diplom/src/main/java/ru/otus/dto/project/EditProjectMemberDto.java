package ru.otus.dto.project;

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
public class EditProjectMemberDto {

    private UserDto user;

    private ProjectRoleEnum roleInProject;

}