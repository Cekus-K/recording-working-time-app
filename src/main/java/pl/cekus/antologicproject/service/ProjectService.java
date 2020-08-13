package pl.cekus.antologicproject.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.form.ProjectCreateForm;
import pl.cekus.antologicproject.form.ProjectFilterForm;
import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.repository.ProjectRepository;
import pl.cekus.antologicproject.specification.ProjectSpecification;
import pl.cekus.antologicproject.utills.Mapper;

import java.time.LocalDate;
import java.util.Optional;

import static pl.cekus.antologicproject.utills.Mapper.mapCreateFormToProject;
import static pl.cekus.antologicproject.utills.Mapper.mapProjectToProjectDto;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    public ProjectDto createProject(ProjectCreateForm projectCreateForm) {
        if (!projectRepository.existsByProjectName(projectCreateForm.getProjectName())) {
            Project toCreate = mapCreateFormToProject(projectCreateForm);
            return mapProjectToProjectDto(projectRepository.save(toCreate));
        }
        return mapProjectToProjectDto(findProjectByProjectName(projectCreateForm.getProjectName()));
    }

    public boolean addEmployeeToProject(String login, String projectName) {
        User user = userService.findUserByLogin(login);
        Project project = findProjectByProjectName(projectName);

        if (user.getRole() == Role.EMPLOYEE) {
            project.addUser(user);
            projectRepository.save(project);
            return true;
        }
        return false;
    }

    public boolean removeEmployeeFromProject(String login, String projectName) {
        User user = userService.findUserByLogin(login);
        Project project = findProjectByProjectName(projectName);

        if (user.getRole() == Role.EMPLOYEE) {
            project.removeUser(user);
            projectRepository.save(project);
            return true;
        }
        return false;
    }

    public Optional<Project> readProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Page<ProjectDto> readProjectsWithFilters(ProjectFilterForm filterForm, Pageable pageable) {
        ProjectSpecification specification = new ProjectSpecification(filterForm);
        return projectRepository.findAll(specification, pageable)
                .map(Mapper::mapProjectToProjectDto);
    }

    public void updateProject(Long id, ProjectCreateForm projectCreateForm) {
        Optional<Project> toUpdate = projectRepository.findById(id);
        toUpdate.ifPresent(project -> {
            checkIfProjectNameAlreadyExists(projectCreateForm.getProjectName());
            setValuesToUpdatingProject(project, projectCreateForm);
            projectRepository.save(project);
        });
    }

    public void deleteProject(Long id) {
        readProjectById(id).ifPresent(project -> project.getUsers()
                .stream()
                .map(User::getLogin)
                .forEach((login) -> removeEmployeeFromProject(login, project.getProjectName())));
        projectRepository.deleteById(id);
    }

    public Project findProjectByProjectName(String projectName) {
        return projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("provided project not found"));
    }

    private void setValuesToUpdatingProject(Project toUpdate, ProjectCreateForm createForm) {
        toUpdate.setProjectName(createForm.getProjectName());
        toUpdate.setDescription(createForm.getDescription());
        toUpdate.setStartDate(createForm.getStartDate());
        toUpdate.setEndDate(createForm.getEndDate());
        toUpdate.setBudget(createForm.getBudget());
    }

    // FIXME: write own exceptions
    private void checkIfProjectNameAlreadyExists(String projectName) {
        if (findProjectByProjectName(projectName) != null) {
            throw new IllegalStateException("provided project name already exists");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createTestProject() {
        createProject(new ProjectCreateForm("test", "test project", LocalDate.now(),
                LocalDate.now().plusDays(100), 10000.00));
    }
}
