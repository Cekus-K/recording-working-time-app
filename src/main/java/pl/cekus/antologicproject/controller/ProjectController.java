package pl.cekus.antologicproject.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<Void> updateProject(@PathVariable UUID uuid, @RequestBody @Valid ProjectCreateForm project) {
        if (projectService.readProjectByUuid(uuid).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        projectService.updateProject(uuid, project);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // TODO: change rest URI
    @DeleteMapping("/projects/{uuid}")
    ResponseEntity<Void> deleteProject(@PathVariable UUID uuid) {
        if (projectService.readProjectByUuid(uuid).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        projectService.deleteProject(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/projects/{projectName}")
    ResponseEntity<Void> addEmployeeToProject(@PathVariable String projectName, @RequestParam(name = "employee") String employeeLogin) {
        boolean assignmentResult = projectService.addEmployeeToProject(employeeLogin, projectName);
        if (assignmentResult) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // TODO: change rest URI
    @DeleteMapping("/projects/{projectName}")
    ResponseEntity<Void> removeEmployeeFromProject(@PathVariable String projectName, @RequestParam(name = "employee") String employeeLogin) {
        boolean assignmentResult = projectService.removeEmployeeFromProject(employeeLogin, projectName);
        if (assignmentResult) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/projects/report")
    ProjectReportDto getProjectReport(@RequestParam(name = "project") String projectName,
                                      @RequestParam(name = "time-period", defaultValue = "all") String timePeriod) {
        return projectService.getProjectReport(projectName, timePeriod);
    }
}
