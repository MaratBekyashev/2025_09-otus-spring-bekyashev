package ru.otus.service;
import ru.otus.dto.EditProjectDto;
import ru.otus.dto.EditProjectMemberDto;
import ru.otus.dto.ProjectDto;
import ru.otus.dto.ProjectMemberDto;
import java.util.List;

public interface ProjectService {

    List<ProjectDto> findAllProjects();

    ProjectDto findProject(Long projectId);

    ProjectDto createProject(EditProjectDto project) ;

    ProjectDto editProject(EditProjectDto project);

    void deleteProject (Long projectId);

    // Project members
    List<ProjectMemberDto> findAllProjectMembers(Long projectId);

    ProjectMemberDto findProjectMember(Long projectId, Long userId);

    ProjectMemberDto addProjectMember(Long projectId, EditProjectMemberDto member);

    ProjectMemberDto editProjectMember(Long projectId,EditProjectMemberDto member);

    void deleteProjectMember(Long projectId, Long userId);




}