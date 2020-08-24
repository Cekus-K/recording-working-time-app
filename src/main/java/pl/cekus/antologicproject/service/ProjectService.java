package pl.cekus.antologicproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.dto.ProjectReportDto;
import pl.cekus.antologicproject.dto.ProjectViewDto;
import pl.cekus.antologicproject.exception.IllegalParameterException;
import pl.cekus.antologicproject.exception.NotFoundException;
import pl.cekus.antologicproject.exception.NotUniqueException;
import pl.cekus.antologicproject.form.ProjectCreateForm;
import pl.cekus.antologicproject.form.ProjectFilterForm;
import pl.cekus.antologicproject.mapper.ProjectMapper;
import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.repository.ProjectRepository;
import pl.cekus.antologicproject.repository.ProjectViewRepository;
import pl.cekus.antologicproject.specification.ProjectViewSpecification;
import pl.cekus.antologicproject.timeperiod.TimePeriod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.cekus.antologicproject.utills.ReportUtil.calculateUserCostInProject;
import static pl.cekus.antologicproject.utills.ReportUtil.calculateUserTimeInProject;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectViewRepository projectViewRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;

    ProjectService(ProjectRepository projectRepository, ProjectViewRepository projectViewRepository, UserService userService, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectViewRepository = projectViewRepository;
        this.userService = userService;
        this.projectMapper = projectMapper;
    }

    public ProjectDto createProject(ProjectCreateForm projectCreateForm) {
        if (projectRepository.existsByProjectName(projectCreateForm.getProjectName())) {
            throw new NotUniqueException("provided project name already exists");
        }
        Project toCreate = projectMapper.mapProjectCreateFormToProject(projectCreateForm);
        return projectMapper.mapProjectToProjectDto(projectRepository.save(toCreate));
    }

    public void addEmployeeToProject(String projectName, String login) {
        User user = userService.findUserByLogin(login);
        Project project = findProjectByProjectName(projectName);
        if (user.getRole() != Role.EMPLOYEE) {
            throw new IllegalParameterException("user assigned to the project must be an employee");
        }
        project.addUser(user);
        projectRepository.save(project);
    }

    public void removeEmployeeFromProject(String login, String projectName) {
        User user = userService.findUserByLogin(login);
        Project project = findProjectByProjectName(projectName);
        if (user.getRole() != Role.EMPLOYEE) {
            throw new IllegalParameterException("user removed from a project must be an employee");
        }
        project.removeUser(user);
        projectRepository.save(project);
    }

    public Optional<Project> readProjectByUuid(UUID uuid) {
        return projectRepository.findByUuid(uuid);
    }

    public Page<ProjectViewDto> readProjectsWithFilters(ProjectFilterForm filterForm, Pageable pageable) {
        ProjectViewSpecification specification = new ProjectViewSpecification(filterForm);
        return projectViewRepository.findAll(specification, pageable)
                .map(projectMapper::mapProjectViewToProjectViewDto);
    }

    public void updateProject(UUID uuid, ProjectCreateForm projectCreateForm) {
        Project toUpdate = projectRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("provided project uuid not found"));
        if (!toUpdate.getProjectName().equals(projectCreateForm.getProjectName())) {
            checkIfProjectNameAlreadyExists(projectCreateForm.getProjectName());
        }
        projectMapper.fromProjectCreateFormToProject(projectCreateForm, toUpdate);
        projectRepository.save(toUpdate);
    }

    public void deleteProject(UUID uuid) {
        Project toDelete = readProjectByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("provided project uuid not found"));
        toDelete.getUsers()
                .stream()
                .map(User::getLogin)
                .forEach((login) -> removeEmployeeFromProject(login, toDelete.getProjectName()));
        projectRepository.deleteByUuid(uuid);
    }

    public ProjectReportDto getProjectReport(String projectName, TimePeriod timePeriod) {
        Project project = findProjectByProjectName(projectName);
        BigDecimal totalProjectCost = getTotalProjectCost(timePeriod, project);
        double totalProjectTime = getTotalProjectTime(timePeriod, project);
        boolean projectExceeded = totalProjectCost.compareTo(BigDecimal.ZERO) < 0;
        List<ProjectReportDto.SingleUserInProjectReport> singleReports = getListOfSingleUserInProjectReports(timePeriod, project);
        return new ProjectReportDto(totalProjectCost.setScale(2, RoundingMode.CEILING),
                totalProjectTime, projectExceeded, singleReports);
    }

    Project findProjectByProjectName(String projectName) {
        return projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new NotFoundException("provided project not found"));
    }

    private BigDecimal getTotalProjectCost(TimePeriod timePeriod, Project project) {
        return project.getUsers().stream()
                .map(user -> calculateUserCostInProject(project, user, timePeriod))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private double getTotalProjectTime(TimePeriod timePeriod, Project project) {
        return project.getUsers().stream()
                .map(user -> calculateUserTimeInProject(project, user, timePeriod))
                .mapToDouble(Double::doubleValue).sum();
    }

    private List<ProjectReportDto.SingleUserInProjectReport> getListOfSingleUserInProjectReports(TimePeriod timePeriod, Project project) {
        return project.getUsers().stream()
                .map(user -> {
                    Double timeInProject = calculateUserTimeInProject(project, user, timePeriod);
                    BigDecimal costInProject = user.getCostPerHour().multiply(BigDecimal.valueOf(timeInProject));
                    return new ProjectReportDto.SingleUserInProjectReport(user.getLogin(), timeInProject,
                            costInProject.setScale(2, RoundingMode.CEILING));
                })
                .collect(Collectors.toList());
    }

    private void checkIfProjectNameAlreadyExists(String projectName) {
        if (projectRepository.existsByProjectName(projectName)) {
            throw new NotUniqueException("provided project name already exists");
        }
    }
}
