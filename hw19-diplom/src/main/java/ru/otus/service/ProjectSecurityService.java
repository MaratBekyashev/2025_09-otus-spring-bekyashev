package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.otus.repository.ProjectMemberRepository;
import ru.otus.repository.ProjectRepository;

@Service("projectSecurityService")
@RequiredArgsConstructor
public class ProjectSecurityService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public boolean isUserProjectOwner(Long projectId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return projectRepository.findById(projectId)
                .map(project -> username.equals(project.getOwner().getUserName()))
                .orElse(false);
    }

    public boolean isUserProjectMember(Long projectId) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return projectMemberRepository.existsByProject_ProjectIdAndUser_UserNameIgnoreCase(projectId, username);
    }
}