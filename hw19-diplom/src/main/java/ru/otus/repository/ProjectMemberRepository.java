package ru.otus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.entity.ProjectMember;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    Optional<ProjectMember> findByProject_ProjectIdAndUser_UserNameIgnoreCase(Long projectId, String username);
    boolean existsByProject_ProjectIdAndUser_UserNameIgnoreCase(Long projectId, String username);
    Optional<ProjectMember> findByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);

    void deleteAllByProject_ProjectId(Long projectId);

}
