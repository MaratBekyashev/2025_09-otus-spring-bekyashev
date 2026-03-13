package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.repository.ProjectMemberRepository;
import ru.otus.repository.ProjectRepository;
import ru.otus.security.CustomUserDetails;

@Service("projectSecurityService")
@RequiredArgsConstructor
public class ProjectSecurityService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public boolean isUserProjectOwner(Long projectId) {
        String username = getPrincipal().getUsername();
        var result = projectRepository.findByProjectId(projectId)
                .map(project -> username.equals(project.getOwner().getLogin()))
                .orElse(false);
        return result;
    }

    public boolean isUserProjectMember(Long projectId) {
        String username = getPrincipal().getUsername();
        return projectMemberRepository.existsByProject_ProjectIdAndUser_LoginIgnoreCase(projectId, username);
    }

    private CustomUserDetails getPrincipal() {
        var result = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return result;
    }
}