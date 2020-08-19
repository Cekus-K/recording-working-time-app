package pl.cekus.antologicproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.dto.ProjectReportDto;
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
import java.util.stream.Collectors;

import static pl.cekus.antologicproject.utills.ReportUtil.TimePeriod;
import static pl.cekus.antologicproject.utills.ReportUtil.calculateUserTimeInProject;


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
            throw new IllegalArgumentException("provided project name already exists");
        }
        Project toCreate = projectMapper.mapProjectCreateFormToProject(projectCreateForm);
        return projectMapper.mapProjectToProjectDto(projectRepository.save(toCreate));
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
                .map(projectMapper::mapProjectToProjectDto)
                .map(this::setBudgetPercentageUse);
    }

    public BigDecimal calculateBudget(String projectName) {
        Project project = findProjectByProjectName(projectName);
        return project.getBudget().subtract(project.getWorkingTimes().stream()
                .map(workingTime -> {
                    BigDecimal costPerHour = workingTime.getUser().getCostPerHour();
                    long userWorkingTimeInSeconds = Duration.between(workingTime.getStartTime(), workingTime.getEndTime()).toSeconds();
                    return costPerHour.multiply(BigDecimal.valueOf(userWorkingTimeInSeconds / 3600));
                }).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public void updateProject(Long id, ProjectCreateForm projectCreateForm) {
        Optional<Project> toUpdate = projectRepository.findById(id);
        toUpdate.ifPresent(project -> {
            if (!project.getProjectName().equals(projectCreateForm.getProjectName())) {
                checkIfProjectNameAlreadyExists(projectCreateForm.getProjectName());
            }
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

    public ProjectReportDto getProjectReport(String projectName, String timePeriod) {
        TimePeriod period = TimePeriod.valueOf(timePeriod.toUpperCase());
        Project project = findProjectByProjectName(projectName);
        BigDecimal totalProjectCost = project.getBudget().subtract(calculateBudget(projectName));
        double totalProjectTime = project.getWorkingTimes().stream().map(workingTime -> Duration.between(workingTime.getStartTime(), workingTime.getEndTime()).toSeconds())
                .mapToDouble(Long::longValue).sum() / 3600;
        boolean projectExceeded = totalProjectCost.compareTo(BigDecimal.ZERO) < 0;
        List<ProjectReportDto.SingleUserInProjectReport> singleReports = project.getUsers().stream()
                .map(user -> {
                    Double timeInProject = calculateUserTimeInProject(project, user, period);
                    BigDecimal costInProject = user.getCostPerHour().multiply(BigDecimal.valueOf(timeInProject));
                    return new ProjectReportDto.SingleUserInProjectReport(user.getLogin(), timeInProject,
                            costInProject.setScale(2, RoundingMode.DOWN));
                })
                .collect(Collectors.toList());
        return new ProjectReportDto(totalProjectCost.setScale(2, RoundingMode.DOWN),
                totalProjectTime, projectExceeded, singleReports);
    }

    private ProjectDto setBudgetPercentageUse(ProjectDto projectDto) {
        BigDecimal budget = projectDto.getBudget();
        BigDecimal remainingBudget = calculateBudget(projectDto.getProjectName());
        if (remainingBudget.equals(budget)) {
            projectDto.setBudgetPercentageUse(BigDecimal.valueOf(0.0));
            return projectDto;
        }
        projectDto.setBudgetPercentageUse(remainingBudget.divide(budget, 3, RoundingMode.DOWN));
        return projectDto;
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
        if (projectRepository.findByProjectName(projectName).isPresent()) {
            throw new IllegalStateException("provided project name already exists");
        }
    }
}
