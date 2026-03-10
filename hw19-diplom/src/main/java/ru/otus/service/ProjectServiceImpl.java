package ru.otus.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.otus.dto.EditProjectDto;
import ru.otus.dto.EditProjectMemberDto;
import ru.otus.dto.ProjectDto;
import ru.otus.dto.ProjectMemberDto;
import ru.otus.entity.Project;
import ru.otus.entity.ProjectMember;
import ru.otus.entity.User;
import ru.otus.exception.EntityNotFoundException;
import ru.otus.model.ProjectRoleEnum;
import ru.otus.repository.ProjectMemberRepository;
import ru.otus.repository.ProjectRepository;
import ru.otus.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private final UserRepository userRepo;

    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findAllProjects() {
        List<Project> dataList = projectRepository.findAll();
        var resultList = ProjectDto.toDtoList(dataList);
        return resultList;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto findProject(Long projectId) {
        var result = ProjectDto.toDto(checkAndGetProject(projectId));
        return result;
    }

    @Override
    @Transactional
    public ProjectDto createProject(EditProjectDto projectDto) {
        var owner = currentUserService.getCurrentUser();
        Project project = EditProjectDto.toDomain(projectDto);
        project.setOwner(owner);
        projectRepository.save(project);

        ProjectMember member = ProjectMember.builder()
                .memberId(null)
                .project(project)
                .user(owner)
                .roleInProject(ProjectRoleEnum.OWNER)
                .build();
        projectMemberRepository.save(member);

        var result = ProjectDto.toDto(project);
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize("@projectSecurityService.isUserProjectOwner(#projectDto.projectId) or hasRole('ADMIN')")
    public ProjectDto editProject(EditProjectDto projectDto) {
        Project project = checkAndGetProject(projectDto.getProjectId());
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());

        projectRepository.save(project);
        ProjectDto result = ProjectDto.toDto(project);
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize("@projectSecurityService.isUserProjectOwner(#projectId) or hasRole('ADMIN')")
    public void deleteProject(Long projectId) {
        Project project = checkAndGetProject(projectId);
        projectMemberRepository.deleteAllByProject_ProjectId(projectId);
        projectRepository.delete(project);
    }

    @Override
    @PreAuthorize("@projectSecurityService.isUserProjectMember(#projectId) or hasRole('ADMIN')")
    @Transactional
    public ProjectMemberDto addProjectMember(Long projectId, EditProjectMemberDto memberDto) {
        Project projectOld = checkAndGetProject(projectId);
        User user = checkAndGetUser(memberDto.getUser().getUserId());
        ProjectMember member = new ProjectMember ();
        member.setMemberId(null);
        member.setUser(user);
        member.setRoleInProject(memberDto.getRoleInProject());
        member.setProject(projectOld);
        if (memberDto.getProject() != null) {
            Project projectNew = checkAndGetProject(memberDto.getProject().getProjectId());
            member.setProject(projectNew);
        }
        boolean memberIsAlreadyExist = projectMemberRepository.existsByProject_ProjectIdAndUser_UserNameIgnoreCase(
                projectId,
                memberDto.getUser().getUserName());

        if (memberIsAlreadyExist) {
            throw new RuntimeException("User already in project");
        }

        projectMemberRepository.save(member);
        var result = ProjectMemberDto.toDto(member);
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize("@projectSecurityService.isUserProjectOwner(#projectId) or hasRole('ADMIN')")
    public ProjectMemberDto editProjectMember(Long projectId,
                                              EditProjectMemberDto memberDto) {
        Project project = checkAndGetProject(projectId);
        ProjectMember member = projectMemberRepository
                .findByProject_ProjectIdAndUser_UserNameIgnoreCase(projectId, memberDto.getUser().getUserName())
                .orElseThrow(() -> {
                    String msg = "Project member not found(projectId=%d, userName=%s)".formatted(
                             projectId,
                             memberDto.getUser().getUserName());
                    return new EntityNotFoundException(msg);
                });
        member.setRoleInProject(memberDto.getRoleInProject());
        projectMemberRepository.save(member);
        return ProjectMemberDto.toDto(member);
    }

    @Override
    @Transactional
    @PreAuthorize("@projectSecurityService.isUserProjectMember(#projectId) or hasRole('ADMIN')")
    public void deleteProjectMember(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository
                .findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() ->new EntityNotFoundException("Member not found(userId=%d)".formatted(userId)));

        projectMemberRepository.delete(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberDto> findAllProjectMembers(Long projectId) {
        List<ProjectMember> dataList = projectMemberRepository.findAll();
        var resultList =  ProjectMemberDto.toDtoList(dataList);
        return resultList;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectMemberDto findProjectMember(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository
                .findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() ->new EntityNotFoundException("Member not found(userId=%d)".formatted(userId)));
       var result = ProjectMemberDto.toDto(member);
       return result;
    }

    private Project checkAndGetProject(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found (%d)".formatted(projectId)));
    }

    private User checkAndGetUser(Long userId) {
        return userRepo
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found (%d)".formatted(userId)));
    }

}