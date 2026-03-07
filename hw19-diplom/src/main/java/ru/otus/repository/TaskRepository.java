package ru.otus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
