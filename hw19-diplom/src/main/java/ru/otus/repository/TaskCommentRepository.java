package ru.otus.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.otus.entity.TaskComment;

import java.util.List;
import java.util.Optional;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long>, JpaSpecificationExecutor<TaskComment> {

    @EntityGraph(attributePaths = {"author", "task"})
    List<TaskComment> findByTask_TaskId(Long taskId);

    @EntityGraph(attributePaths = {"author", "task"})
    Optional<TaskComment> findById(Long commentId);

    boolean existsByCommentId(Long commentId);
}
