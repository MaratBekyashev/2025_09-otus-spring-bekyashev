package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.CreateProjectRequestDto;
import ru.otus.dto.ProjectDto;
import ru.otus.entity.Project;
import ru.otus.security.CustomUserDetails;
import ru.otus.service.ProjectServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectServiceImpl projectService;

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> findAllProjects(){
        var resultList = projectService.getAllProjects();
        return ResponseEntity.ok(resultList);
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectDto> createProject(@RequestBody CreateProjectRequestDto request,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        ProjectDto project = projectService.createProject(
                request.name(),
                request.description(),
                userDetails.getUser());
        return ResponseEntity.ok(project);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectDto> findProjectById(@PathVariable("projectId") Long projectId) {
        ProjectDto project = projectService.findProject(projectId);
        return ResponseEntity.ok(project);
    }


}