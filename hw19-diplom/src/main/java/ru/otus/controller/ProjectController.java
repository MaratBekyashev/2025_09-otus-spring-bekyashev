package ru.otus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.project.CreateProjectDto;
import ru.otus.dto.project.CreateProjectMemberDto;
import ru.otus.model.projects.CreateProjectRequest;
import ru.otus.dto.project.EditProjectDto;
import ru.otus.dto.project.EditProjectMemberDto;
import ru.otus.dto.project.ProjectDto;
import ru.otus.dto.project.ProjectMemberDto;
import ru.otus.model.projects.UpdateProjectRequest;
import ru.otus.service.ProjectService;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> findAllProjects(){
        var resultList = projectService.findAllProjects();
        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> findProjectById(@PathVariable("projectId") Long projectId) {
        ProjectDto project = projectService.findProject(projectId);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody @Valid CreateProjectRequest request) {
        var createProjectDto = CreateProjectDto.builder()
                .name(request.name())
                .description(request.description())
                .build();
        ProjectDto response = projectService.createProject(createProjectDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> editProject(@PathVariable("projectId") Long projectId,
                                                  @RequestBody UpdateProjectRequest request) {
        var editProjectDto = EditProjectDto.builder()
                .projectId(projectId)
                .name(request.name())
                .description(request.description())
                .build();
        ProjectDto editedProject = projectService.editProject(editProjectDto);
        return ResponseEntity.ok(editedProject);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable("projectId") Long projectId) {
        projectService.deleteProject(projectId);
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberDto>> findAllProjectMembers(@PathVariable("projectId") Long projectId){
        List<ProjectMemberDto> resultList = projectService.findAllProjectMembers(projectId);
        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<ProjectMemberDto> findProjectMember(@PathVariable("projectId") Long projectId,
                                                              @PathVariable("memberId") Long userId){
        ProjectMemberDto result = projectService.findProjectMember(projectId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{projectId}/members")
    public ProjectMemberDto addProjectMember(@PathVariable Long projectId,
                                             @RequestBody CreateProjectMemberDto member) {
        var createdProjectMember = projectService.addProjectMember(projectId, member);
        return createdProjectMember;
    }

    @PutMapping("/{projectId}/members")
    public ProjectMemberDto editProjectMember(@PathVariable("projectId") Long projectId,
                                              @RequestBody EditProjectMemberDto projectMemberDto) {
        ProjectMemberDto result = projectService.editProjectMember(projectId, projectMemberDto);
        return result;
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public void deleteProjectMember(@PathVariable("projectId") Long projectId,
                                    @PathVariable("userId") Long userId) {
        projectService.deleteProjectMember(projectId, userId);
    }

}