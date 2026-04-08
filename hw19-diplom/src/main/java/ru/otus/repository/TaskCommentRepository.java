package ru.otus.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.entity.TaskComment;
import java.util.List;
import java.util.Optional;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long>, JpaSpecificationExecutor<TaskComment> {

    @EntityGraph(attributePaths = {"author", "task"})
    List<TaskComment> findByTask_TaskId(Long taskId);

    @EntityGraph(attributePaths = {"author", "task"})
    Optional<TaskComment> findById(Long commentId);

    boolean existsByCommentId(Long commentId);

    @Modifying
    @Query (value = """
           DELETE FROM task_comments WHERE task_id = :p_task_id RETURNING *
    """, nativeQuery = true)
    List<TaskComment> deleteCommentsByTaskAndGet(@Param("p_task_id") Long taskId);
}
