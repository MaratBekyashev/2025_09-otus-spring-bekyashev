package ru.otus.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.entity.ProjectMember;
import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @EntityGraph(attributePaths = {"project"})
    List<ProjectMember> findAllByProject_ProjectIdAndUser_IsDeletedIsNull(Long projectId);

    boolean existsByProject_ProjectIdAndUser_LoginIgnoreCase(Long projectId, String login);

    Optional<ProjectMember> findByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);

    @Modifying
    @Query("""
      delete from ProjectMember p where p.project.projectId = :project_id
    """)
    void deleteMembersByProjectId(@Param("project_id") Long projectId);

}
