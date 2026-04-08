package ru.otus.service;

import ru.otus.dto.project.CreateProjectDto;
import ru.otus.dto.project.CreateProjectMemberDto;
import ru.otus.dto.project.EditProjectDto;
import ru.otus.dto.project.EditProjectMemberDto;
import ru.otus.dto.project.ProjectDto;
import ru.otus.dto.project.ProjectMemberDto;
import java.util.List;

public interface ProjectService {

    List<ProjectDto> findAllProjects();

    ProjectDto findProject(Long projectId);

    ProjectDto createProject(CreateProjectDto project) ;

    ProjectDto editProject(EditProjectDto project);

    void deleteProject (Long projectId);

    // Project members
    List<ProjectMemberDto> findAllProjectMembers(Long projectId);

    ProjectMemberDto findProjectMember(Long projectId, Long userId);

    ProjectMemberDto addProjectMember(Long projectId, CreateProjectMemberDto member);

    ProjectMemberDto editProjectMember(Long projectId,EditProjectMemberDto member);

    void deleteProjectMember(Long projectId, Long userId);

}