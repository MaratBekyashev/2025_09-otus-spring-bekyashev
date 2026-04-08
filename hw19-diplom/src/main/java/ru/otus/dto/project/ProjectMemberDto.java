package ru.otus.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.dto.UserDto;
import ru.otus.entity.ProjectMember;
import ru.otus.model.IdentifableEntity;
import ru.otus.model.ProjectRoleEnum;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberDto implements IdentifableEntity {

    private Long memberId;

    private ProjectDto project;

    private UserDto user;

    private ProjectRoleEnum roleInProject;

    public static ProjectMember toDomain (ProjectMemberDto item) {
        var result = ProjectMember.builder()
                .memberId(item.memberId)
                .project(ProjectDto.toDomain(item.project))
                .user(UserDto.toDomain(item.user))
                .roleInProject(item.roleInProject)
                .build();
        return result;
    }

    public static ProjectMemberDto toDto (ProjectMember dataItem) {
        var result = ProjectMemberDto.builder()
                .memberId(dataItem.getMemberId())
                .user(UserDto.toDto(dataItem.getUser()))
                .project(ProjectDto.toDto(dataItem.getProject()))
                .roleInProject(dataItem.getRoleInProject())
                .build();
        return result;
    }

    public static List<ProjectMemberDto> toDtoList (List<ProjectMember> dataList) {
        return dataList.stream().map(ProjectMemberDto::toDto).toList();
    }

    @Override
    public Long getId() {
        return this.memberId;
    }
}