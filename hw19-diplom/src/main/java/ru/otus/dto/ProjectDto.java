package ru.otus.dto;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.entity.Project;
import ru.otus.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class ProjectDto {

    private Long projectId;

    private String name;

    private String description;

    private UserDto owner;

    private LocalDateTime createDate;

    public static ProjectDto toDto (Project project) {
        var result = new ProjectDto();

        result.setProjectId(project.getProjectId());
        result.setName(project.getName());
        result.setDescription(project.getDescription());
        result.setOwner(new UserDto(project.getOwner().getUserName(), project.getOwner().getEmail()));

        return result;
    }

    public static List<ProjectDto> toDtoList (List<Project> projectList) {
        return projectList.stream().map(ProjectDto::toDto).toList();
    }

}
