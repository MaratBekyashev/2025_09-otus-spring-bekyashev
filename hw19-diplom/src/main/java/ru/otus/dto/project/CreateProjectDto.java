package ru.otus.dto.project;

import jakarta.validation.constraints.NotBlank;
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
public class CreateProjectDto {

    @NotBlank(message = "Имя проекта должно быть указано")
    private String name;

    private String description;

    public static Project toDomain (CreateProjectDto projectDto) {
        var result = Project.builder()
                .projectId(null)
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .createDate(LocalDateTime.now())
                .build();
        return result;
    }

    public static CreateProjectDto toDto (Project project) {
        var result = new CreateProjectDto();

        //result.setProjectId(project.getProjectId());
        result.setName(project.getName());
        result.setDescription(project.getDescription());

        return result;
    }

    public static List<CreateProjectDto> toDtoList (List<Project> projectList) {
        return projectList.stream().map(CreateProjectDto::toDto).toList();
    }

}
