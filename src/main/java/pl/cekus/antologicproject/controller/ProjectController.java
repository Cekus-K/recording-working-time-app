package pl.cekus.antologicproject.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.form.ProjectCreateForm;
import pl.cekus.antologicproject.form.ProjectFilterForm;
import pl.cekus.antologicproject.service.ProjectService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
class ProjectController extends ResponseEntityExceptionHandler {

    private final ProjectService projectService;

    ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/project")
    ProjectDto createProject(@RequestBody @Valid ProjectCreateForm project) {
        return projectService.createProject(project);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/projects")
    Page<ProjectDto> filterProject(@RequestBody ProjectFilterForm projectFilterForm, Pageable pageable) {
        return projectService.readProjectsWithFilters(projectFilterForm, pageable);
    }

    @PutMapping("/project/{projectName}/add")
    ResponseEntity<Void> addEmployeeToProject(@PathVariable String projectName, @RequestParam(name = "employee") String employeeLogin) {
        boolean assignmentResult = projectService.addEmployeeToProject(employeeLogin, projectName);
        if (assignmentResult) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/project/{projectName}/remove")
    ResponseEntity<Void> removeEmployeeFromProject(@PathVariable String projectName, @RequestParam(name = "employee") String employeeLogin) {
        boolean assignmentResult = projectService.removeEmployeeFromProject(employeeLogin, projectName);
        if (assignmentResult) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/project/{id}")
    ResponseEntity<Void> updateProject(@PathVariable Long id, @RequestBody @Valid ProjectCreateForm project) {
        if (projectService.readProjectById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        projectService.updateProject(id, project);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/project/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (projectService.readProjectById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
