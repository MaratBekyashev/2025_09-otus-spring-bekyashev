package ru.otus.service;

import ru.otus.dto.ProjectDto;
import ru.otus.entity.Project;
import ru.otus.entity.User;

import java.util.List;


public interface ProjectService {

    List<ProjectDto> getAllProjects();

    ProjectDto findProject(Long projectId);

    ProjectDto createProject(String name, String description, User owner) ;

    void addMember ();

}