package ru.otus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.model.Project;
import ru.otus.model.Task;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
