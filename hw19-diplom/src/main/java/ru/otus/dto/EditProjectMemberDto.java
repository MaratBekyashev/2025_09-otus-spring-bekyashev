package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.entity.ProjectMember;
import ru.otus.model.ProjectRoleEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditProjectMemberDto {

    private ProjectDto project;

    private UserDto user;

    private ProjectRoleEnum roleInProject;

}