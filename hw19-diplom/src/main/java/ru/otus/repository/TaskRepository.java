package ru.otus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.model.Task;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
