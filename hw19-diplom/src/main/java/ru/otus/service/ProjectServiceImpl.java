package ru.otus.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.otus.dto.UserDto;
import ru.otus.dto.project.CreateProjectDto;
import ru.otus.dto.project.CreateProjectMemberDto;
import ru.otus.dto.project.EditProjectDto;
import ru.otus.dto.project.EditProjectMemberDto;
import ru.otus.dto.project.ProjectDto;
import ru.otus.dto.project.ProjectMemberDto;
import ru.otus.entity.Project;
import ru.otus.entity.ProjectMember;
import ru.otus.entity.User;
import ru.otus.exception.CommonBusinessException;
import ru.otus.exception.EntityNotFoundException;
import ru.otus.exception.ServiceNotAvailableException;
import ru.otus.exception.UserAlreadyExistException;
import ru.otus.model.ProjectRoleEnum;
import ru.otus.repository.ProjectMemberRepository;
import ru.otus.repository.ProjectRepository;
import ru.otus.repository.UserRepository;
import java.util.List;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;

    private final ProjectMemberRepository projectMemberRepository;

    private final UserRepository userRepo;

    private final AuthService authService;

    private final Counter projectsCreated;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ProjectMemberRepository projectMemberRepository,
                              UserRepository userRepo,
                              AuthService authService,
                              MeterRegistry registry) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepo = userRepo;
        this.authService = authService;
        this.projectsCreated = Counter
                .builder("projects.currentCount")
                .description("Quantity of active projects")
                .register(registry);
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackFindAllProjects")
    public List<ProjectDto> findAllProjects() {
        List<Project> dataList = projectRepository.findAll();
        var resultList = ProjectDto.toDtoList(dataList);
        return resultList;
    }

    private List<ProjectDto> fallbackFindAllProjects(Throwable ex) throws ServiceNotAvailableException {
        log.error("Fallback triggered for findAllProjects", ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackFindProject")
    public ProjectDto findProject(Long projectId) {
        var result = ProjectDto.toDto(checkAndGetProject(projectId));
        return result;
    }

    private ProjectDto fallbackFindProject(Long projectId, Throwable ex)
            throws ServiceNotAvailableException {
        log.error("Fallback triggered for findProject(projectId={})",projectId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackCreateProject")
    public ProjectDto createProject(CreateProjectDto projectDto) {
        UserDto owner = authService.getCurrentUser();
        User user = UserDto.toDomain(owner);
        Project project = CreateProjectDto.toDomain(projectDto);
        project.setOwner(user);
        projectRepository.save(project);

        ProjectMember member = ProjectMember.builder()
                .memberId(null)
                .project(project)
                .user(user)
                .roleInProject(ProjectRoleEnum.OWNER)
                .build();
        projectMemberRepository.save(member);
        projectsCreated.increment();

        var result = ProjectDto.toDto(project);

        return result;
    }

    private ProjectDto fallbackCreateProject(CreateProjectDto projectDto, Throwable ex)
            throws ServiceNotAvailableException {
        log.error("Fallback triggered for createProject(project={})", projectDto, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @PreAuthorize("@projectPolicy.isUserProjectOwner(#projectDto.projectId) or hasRole('ADMIN')")
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackEditProject")
    public ProjectDto editProject(EditProjectDto projectDto) {
        Project project = checkAndGetProject(projectDto.getProjectId());
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());

        projectRepository.save(project);
        ProjectDto result = ProjectDto.toDto(project);
        return result;
    }

    private ProjectDto fallbackEditProject(EditProjectDto projectDto, Throwable ex)
            throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for editProject(project={})", projectDto, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @PreAuthorize("@projectPolicy.isUserProjectOwner(#projectId) or hasRole('ADMIN')")
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackDeleteProject")
    public void deleteProject(Long projectId) {
        Project project = checkAndGetProject(projectId);
        projectMemberRepository.deleteMembersByProjectId(projectId);
        projectRepository.delete(project);
    }

    private void fallbackDeleteProject(Long projectId, Throwable ex)
            throws ServiceNotAvailableException {
        log.error("Fallback triggered for deleteProject(projectId={})", projectId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @PreAuthorize("@projectPolicy.isUserProjectMember(#projectId) or hasRole('ADMIN')")
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackAddProjectMember")
    public ProjectMemberDto addProjectMember(Long projectId, CreateProjectMemberDto memberDto) {
        Project project = checkAndGetProject(projectId);
        User user = checkAndGetUser(memberDto.getUser().getUserId());
        ProjectMember member = new ProjectMember();
        member.setMemberId(null);
        member.setUser(user);
        member.setRoleInProject(memberDto.getRoleInProject());
        member.setProject(project);
        boolean memberIsAlreadyExist = projectMemberRepository.existsByProject_ProjectIdAndUser_LoginIgnoreCase(
                projectId,
                user.getLogin());

        if (memberIsAlreadyExist) {
            throw new UserAlreadyExistException("User already in project(user=%s)".formatted(user.getLogin()));
        }

        projectMemberRepository.save(member);
        var result = ProjectMemberDto.toDto(member);
        return result;
    }

    private ProjectMemberDto fallbackAddProjectMember(Long projectId, CreateProjectMemberDto memberDto, Throwable ex)
        throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for addProjectMember(projectId={}, member={})", projectId, memberDto, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @PreAuthorize("@projectPolicy.isUserProjectOwner(#projectId) or hasRole('ADMIN')")
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackEditProjectMember")
    public ProjectMemberDto editProjectMember(Long projectId,
                                              EditProjectMemberDto memberDto) {
        Project project = checkAndGetProject(projectId);
        ProjectMember member = projectMemberRepository
                .findByProject_ProjectIdAndUser_UserId(projectId, memberDto.getUser().getUserId())
                .orElseThrow(() -> {
                    String msg = "Project member not found(projectId=%d, login=%s)".formatted(
                             projectId,
                             memberDto.getUser().getLogin());
                    return new EntityNotFoundException(msg);
                });
        member.setRoleInProject(memberDto.getRoleInProject());
        projectMemberRepository.save(member);
        return ProjectMemberDto.toDto(member);
    }

    private ProjectMemberDto fallbackEditProjectMember(Long projectId, EditProjectMemberDto memberDto, Throwable ex)
            throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for editProjectMember(projectId={}, member={})", projectId, memberDto, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @PreAuthorize("@projectPolicy.isUserProjectMember(#projectId) or hasRole('ADMIN')")
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackDeleteProjectMember")
    public void deleteProjectMember(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository
                .findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() ->new EntityNotFoundException("Member not found(userId=%d)".formatted(userId)));

        projectMemberRepository.delete(member);
    }

    private void fallbackDeleteProjectMember(Long projectId, Long userId, Throwable ex)
            throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for deleteProjectMember(projectId={}, user={})", projectId, userId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackFindAllProjectMembers")
    public List<ProjectMemberDto> findAllProjectMembers(Long projectId) {
        var project = checkAndGetProject(projectId);
        List<ProjectMember> dataList = projectMemberRepository.findAllByProject_ProjectIdAndUser_IsDeletedIsNull(projectId);
        var resultList =  ProjectMemberDto.toDtoList(dataList);
        return resultList;
    }

    private List<ProjectMemberDto> fallbackFindAllProjectMembers(Long projectId, Throwable ex)
            throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for deleteProjectMember(projectId={})", projectId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackFindProjectMember")
    public ProjectMemberDto findProjectMember(Long projectId, Long userId) {
        var project = checkAndGetProject(projectId);
        ProjectMember member = projectMemberRepository
                .findByProject_ProjectIdAndUser_UserId(project.getProjectId(), userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found(userId=%d)".formatted(userId)));
       var result = ProjectMemberDto.toDto(member);
       return result;
    }

    private ProjectMemberDto fallbackFindProjectMember(Long projectId, Long userId, Throwable ex)
            throws ServiceNotAvailableException {
        if (ex instanceof CommonBusinessException e) {
            throw e;
        }
        log.error("Fallback triggered for deleteProjectMember(projectId={}, userId={})", projectId, userId, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    private Project checkAndGetProject(Long projectId) {
        return projectRepository
                .findByProjectId(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found (%d)".formatted(projectId)));
    }

    private User checkAndGetUser(Long userId) {
        return userRepo
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found (%d)".formatted(userId)));
    }

}