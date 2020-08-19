package pl.cekus.antologicproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.dto.ProjectReportDto;
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
import pl.cekus.antologicproject.specification.ProjectSpecification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.cekus.antologicproject.utills.ReportUtil.*;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;

    ProjectService(ProjectRepository projectRepository, UserService userService, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectMapper = projectMapper;
    }

    public ProjectDto createProject(ProjectCreateForm projectCreateForm) {
        if (projectRepository.findByProjectName(projectCreateForm.getProjectName()).isPresent()) {
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

    public Page<ProjectDto> readProjectsWithFilters(ProjectFilterForm filterForm, Pageable pageable) {
        ProjectSpecification specification = new ProjectSpecification(filterForm);
        return projectRepository.findAll(specification, pageable)
                .map(projectMapper::mapProjectToProjectDto)
                .map(this::calculatePercentageBudget);
    }

    public void updateProject(UUID uuid, ProjectCreateForm projectCreateForm) {
        Project toUpdate = projectRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("provided project uuid not found"));
        if (!toUpdate.getProjectName().equals(projectCreateForm.getProjectName())) {
            checkIfProjectNameAlreadyExists(projectCreateForm.getProjectName());
        }
        setValuesToUpdatingProject(toUpdate, projectCreateForm);
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

    public Project findProjectByProjectName(String projectName) {
        return projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new NotFoundException("provided project not found"));
    }

    public ProjectReportDto getProjectReport(String projectName, String timePeriod) {
        TimePeriod period;
        try {
            period = TimePeriod.valueOf(timePeriod.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalParameterException("invalid time period type was provided");
        }
        Project project = findProjectByProjectName(projectName);
        BigDecimal totalProjectCost = project.getUsers().stream()
                .map(user -> calculateUserCostInProject(project, user, period))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double totalProjectTime = project.getUsers().stream()
                .map(user -> calculateUserTimeInProject(project, user, period))
                .mapToDouble(Double::doubleValue).sum();
        boolean projectExceeded = totalProjectCost.compareTo(BigDecimal.ZERO) < 0;
        List<ProjectReportDto.SingleUserInProjectReport> singleReports = project.getUsers().stream()
                .map(user -> {
                    Double timeInProject = calculateUserTimeInProject(project, user, period);
                    BigDecimal costInProject = user.getCostPerHour().multiply(BigDecimal.valueOf(timeInProject));
                    return new ProjectReportDto.SingleUserInProjectReport(user.getLogin(), timeInProject,
                            costInProject.setScale(2, RoundingMode.CEILING));
                })
                .collect(Collectors.toList());
        return new ProjectReportDto(totalProjectCost.setScale(2, RoundingMode.CEILING),
                totalProjectTime, projectExceeded, singleReports);
    }

    private ProjectDto calculatePercentageBudget(ProjectDto projectDto) {
        projectDto.setBudgetPercentageUse(calculateBudget(projectDto.getProjectName())
                .multiply(BigDecimal.valueOf(100))
                .divide(projectDto.getBudget(), 2, RoundingMode.CEILING));
        return projectDto;
    }

    private BigDecimal calculateBudget(String projectName) {
        Project project = findProjectByProjectName(projectName);
        return BigDecimal.valueOf(project.getWorkingTimes().stream()
                .map(workingTime -> {
                    BigDecimal costPerHour = workingTime.getUser().getCostPerHour();
                    double userWorkingTimeInSeconds = Duration.between(workingTime.getStartTime(), workingTime.getEndTime()).toSeconds();
                    return costPerHour.multiply(BigDecimal.valueOf(userWorkingTimeInSeconds / 3600).setScale(2, RoundingMode.CEILING));
                }).mapToDouble(BigDecimal::doubleValue).sum());
    }

    private void setValuesToUpdatingProject(Project toUpdate, ProjectCreateForm createForm) {
        toUpdate.setProjectName(createForm.getProjectName());
        toUpdate.setDescription(createForm.getDescription());
        toUpdate.setStartDate(createForm.getStartDate());
        toUpdate.setEndDate(createForm.getEndDate());
        toUpdate.setBudget(createForm.getBudget());
    }

    private void checkIfProjectNameAlreadyExists(String projectName) {
        if (projectRepository.findByProjectName(projectName).isPresent()) {
            throw new NotUniqueException("provided project name already exists");
        }
    }
}
