package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.dto.ProjectDto;
import ru.otus.entity.Project;
import ru.otus.entity.ProjectMember;
import ru.otus.entity.User;
import ru.otus.model.ProjectRole;
import ru.otus.repository.ProjectMemberRepository;
import ru.otus.repository.ProjectRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public List<ProjectDto> getAllProjects() {
        List<Project> dataList = projectRepository.findAll();
        var resultList = ProjectDto.toDtoList(dataList);
        return resultList;
    }

    @Override
    public ProjectDto findProject(Long projectId) {
        var project = projectRepository
                .findById(projectId)
                .orElseThrow(()-> new RuntimeException("Project not found(id=%d)".formatted(projectId)));
        return ProjectDto.toDto(project);
    }

    @Override
    public ProjectDto createProject(String name,
                                    String description,
                                    User owner) {
        Project project = Project.builder()
                .name(name)
                .description(description)
                .owner(owner)
                .createDate(LocalDateTime.now())
                .build();

        Project savedProject = projectRepository.save(project);

        ProjectMember member = ProjectMember.builder()
                .project(savedProject)
                .user(owner)
                .roleInProject(ProjectRole.OWNER)
                .build();

        projectMemberRepository.save(member);

        return ProjectDto.toDto(savedProject);
    }

    @Override
    public void addMember() {

    }

}