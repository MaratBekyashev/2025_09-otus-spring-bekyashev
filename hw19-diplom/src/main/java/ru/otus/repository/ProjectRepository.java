package ru.otus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
