package ru.otus.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.dto.UserDto;
import ru.otus.entity.Project;
import ru.otus.entity.User;
import ru.otus.model.IdentifableEntity;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ProjectDto implements IdentifableEntity {

    private Long projectId;

    private String name;

    private String description;

    private UserDto owner;

    private LocalDateTime createDate;

    public static Project toDomain (ProjectDto project) {
        User owner  = User.builder()
                .userId(project.getOwner().getUserId())
                .login(project.getOwner().getLogin())
                .userName(project.getOwner().getUserName())
                .build();

        var result = Project.builder()
                .projectId(project.getProjectId())
                .name(project.getName())
                .description(project.getDescription())
                .owner(owner)
                .createDate(LocalDateTime.now())
                .build();
        return result;
    }

    public static ProjectDto toDto (Project project) {
        var result = new ProjectDto();

        var owner = UserDto.builder()
                .userId(project.getOwner().getUserId())
                .login(project.getOwner().getLogin())
                .userName(project.getOwner().getUserName())
                .email(project.getOwner().getEmail())
                .build();
        result.setProjectId(project.getProjectId());
        result.setName(project.getName());
        result.setDescription(project.getDescription());
        result.setOwner(owner);
        result.setCreateDate(project.getCreateDate());

        return result;
    }

    public static List<ProjectDto> toDtoList (List<Project> projectList) {
        return projectList.stream().map(ProjectDto::toDto).toList();
    }

    @Override
    public Long getId() {
        return this.projectId;
    }
}
