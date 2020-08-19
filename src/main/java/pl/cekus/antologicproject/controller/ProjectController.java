package pl.cekus.antologicproject.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.dto.ProjectReportDto;
import pl.cekus.antologicproject.form.ProjectCreateForm;
import pl.cekus.antologicproject.form.ProjectFilterForm;
import pl.cekus.antologicproject.service.ProjectService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api")
class ProjectController extends ResponseEntityExceptionHandler {

    private final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/projects")
    ProjectDto createProject(@RequestBody @Valid ProjectCreateForm project) {
        return projectService.createProject(project);
    }

    @GetMapping("/projects")
    Page<ProjectDto> filterProject(@RequestBody ProjectFilterForm projectFilterForm, Pageable pageable) {
        return projectService.readProjectsWithFilters(projectFilterForm, pageable);
    }

    @PutMapping("/projects/{uuid}")
    void updateProject(@PathVariable UUID uuid, @RequestBody @Valid ProjectCreateForm project) {
        projectService.updateProject(uuid, project);
    }

    @DeleteMapping("/projects/{uuid}")
    void deleteProject(@PathVariable UUID uuid) {
        projectService.deleteProject(uuid);
    }

    @PutMapping("/projects")
    void addEmployeeToProject(@RequestParam(name = "project-name") String projectName,
                              @RequestParam(name = "employee") String employeeLogin) {
        projectService.addEmployeeToProject(projectName, employeeLogin);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/projects")
    void removeEmployeeFromProject(@RequestParam(name = "project-name") String projectName,
                                   @RequestParam(name = "employee") String employeeLogin) {
        projectService.removeEmployeeFromProject(employeeLogin, projectName);
    }

    @GetMapping("/projects/report")
    ProjectReportDto getProjectReport(@RequestParam(name = "project") String projectName,
                                      @RequestParam(name = "time-period", defaultValue = "all") String timePeriod) {
        return projectService.getProjectReport(projectName, timePeriod);
    }
}
