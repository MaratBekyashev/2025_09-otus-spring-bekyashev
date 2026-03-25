package ru.otus.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.repository.ProjectMemberRepository;
import ru.otus.repository.ProjectRepository;

@Service("projectPolicy")
@RequiredArgsConstructor
public class ProjectPolicy {

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
        var result = projectMemberRepository.existsByProject_ProjectIdAndUser_LoginIgnoreCase(projectId, username);
        return result;
    }

    private CustomUserDetails getPrincipal() {
        var result = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return result;
    }
}