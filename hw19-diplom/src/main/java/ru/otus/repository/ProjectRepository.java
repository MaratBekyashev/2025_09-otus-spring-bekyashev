package ru.otus.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.entity.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("select p from Project p left join fetch p.owner")
    List<Project> findAllWithOwner();

    @EntityGraph(attributePaths={"owner"})
    Optional<Project> findByProjectId(Long projectId);

}
