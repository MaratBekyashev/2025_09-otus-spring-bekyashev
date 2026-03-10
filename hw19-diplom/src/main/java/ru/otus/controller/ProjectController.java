package ru.otus.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.CreateProjectRequestDto;
import ru.otus.dto.EditProjectDto;
import ru.otus.dto.EditProjectMemberDto;
import ru.otus.dto.ProjectDto;
import ru.otus.dto.ProjectMemberDto;
import ru.otus.dto.UserDto;
import ru.otus.security.CustomUserDetails;
import ru.otus.service.ProjectServiceImpl;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectServiceImpl projectService;

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
    public ResponseEntity<ProjectDto> createProject(@RequestBody CreateProjectRequestDto request) {
        var editProjectDto = EditProjectDto.builder()
                .projectId(null)
                .name(request.name())
                .description(request.description())
                .build();
        ProjectDto createdProject = projectService.createProject(editProjectDto);
        return ResponseEntity.ok(createdProject);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> editProject(@PathVariable Long projectId,
                                                  @RequestBody  CreateProjectRequestDto request,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        var editProjectDto = EditProjectDto.builder()
                .projectId(projectId)
                .name(request.name())
                .description(request.description())
                .build();
        ProjectDto editedPproject = projectService.editProject(editProjectDto);
        return ResponseEntity.ok(editedPproject);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(Long projectId) {
        projectService.deleteProject(projectId);
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberDto>> findAllProjectMembers(@PathVariable("projectId") Long projectId){
        List<ProjectMemberDto> resultList = projectService.findAllProjectMembers(projectId);
        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectMemberDto> findAllProjectMembers(@PathVariable("projectId") Long projectId,
                                                                  @PathVariable("userId") Long userId){
        ProjectMemberDto result = projectService.findProjectMember(projectId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{projectId}/members")
    public ProjectMemberDto addProjectMember(@PathVariable Long projectId,
                                             @RequestBody EditProjectMemberDto member) {
        EditProjectMemberDto projectMember = EditProjectMemberDto.builder()
                .project(member.getProject())
                .user(member.getProject().getOwner())
                .roleInProject(member.getRoleInProject())
                .build();
        var createdProjectMember = projectService.addProjectMember(projectId, projectMember);
        return createdProjectMember;
    }

    @PutMapping("/{projectId}/members")
    public ProjectMemberDto editProjectMember(@PathVariable Long projectId,
                                              @RequestBody EditProjectMemberDto projectMemberDto) {
        EditProjectMemberDto projectMember = EditProjectMemberDto.builder()
                .project(projectMemberDto.getProject())
                .user(projectMemberDto.getUser())
                .roleInProject(projectMemberDto.getRoleInProject())
                .build();
        ProjectMemberDto result = projectService.editProjectMember(projectId, projectMemberDto);
        return result;
    }

    @DeleteMapping("/projects/{projectId}/members/{userId}")
    public void deleteProjectMember(@PathVariable("projectId") Long projectId,
                                    @PathVariable("userId") Long userId) {
        projectService.deleteProjectMember(projectId, userId);
    }

}