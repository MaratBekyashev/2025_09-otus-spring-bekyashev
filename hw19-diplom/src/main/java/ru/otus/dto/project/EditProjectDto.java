package ru.otus.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.otus.entity.Project;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class EditProjectDto {

    private Long projectId;

    private String name;

    private String description;

    public static Project toDomain (EditProjectDto projectDto) {
        var result = Project.builder()
                .projectId(projectDto.getProjectId())
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .createDate(LocalDateTime.now())
                .build();
        return result;
    }

    public static EditProjectDto toDto (Project project) {
        var result = new EditProjectDto();

        result.setProjectId(project.getProjectId());
        result.setName(project.getName());
        result.setDescription(project.getDescription());

        return result;
    }

    public static List<EditProjectDto> toDtoList (List<Project> projectList) {
        return projectList.stream().map(EditProjectDto::toDto).toList();
    }

}
