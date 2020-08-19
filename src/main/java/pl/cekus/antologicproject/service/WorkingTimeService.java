package pl.cekus.antologicproject.service;

import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.WorkingTimeDto;
import pl.cekus.antologicproject.form.WorkingTimeCreateForm;
import pl.cekus.antologicproject.mapper.WorkingTimeMapper;
import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.model.WorkingTime;
import pl.cekus.antologicproject.repository.WorkingTimeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkingTimeService {

    private final WorkingTimeRepository workingTimeRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final WorkingTimeMapper workingTimeMapper;

    WorkingTimeService(WorkingTimeRepository workingTimeRepository, UserService userService,
                       ProjectService projectService, WorkingTimeMapper workingTimeMapper) {
        this.workingTimeRepository = workingTimeRepository;
        this.userService = userService;
        this.projectService = projectService;
        this.workingTimeMapper = workingTimeMapper;
    }

    // TODO: validation of time period overlapping
    public void addWorkingTimeToUser(WorkingTimeCreateForm createForm) {
        User user = userService.findUserByLogin(createForm.getUserLogin());
        Project project = projectService.findProjectByProjectName(createForm.getProjectName());
        if (!user.getProjects().contains(project)) {
            throw new IllegalArgumentException("the given employee is not assigned to given project");
        }
        WorkingTime workingTime = new WorkingTime(createForm.getStartTime(), createForm.getEndTime(), user, project);
        user.addWorkingTime(workingTime);
        workingTimeRepository.save(workingTime);
    }

    public List<WorkingTimeDto> readAllWorkingTimes(String login) {
        User user = userService.findUserByLogin(login);
        return user.getWorkingTimes().stream()
                .map(workingTimeMapper::mapWorkingTimeToWorkingTimeDto)
                .collect(Collectors.toList());
    }

    // TODO: validation of time period overlapping
    public void updateWorkingTime(Long id, WorkingTimeCreateForm workingTimeCreateForm) {
        WorkingTime toUpdate = workingTimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("provided working time id not found"));
        setValuesToUpdatingWorkingTime(toUpdate, workingTimeCreateForm);
        workingTimeRepository.save(toUpdate);
    }

    public void deleteWorkingTime(Long id) {
        WorkingTime toDelete = workingTimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("provided working time id not found"));
        User user = toDelete.getUser();
        user.removeWorkingTime(toDelete);
        workingTimeRepository.delete(toDelete);
    }

    private void setValuesToUpdatingWorkingTime(WorkingTime toUpdate, WorkingTimeCreateForm createForm) {
        toUpdate.setStartTime(createForm.getStartTime());
        toUpdate.setEndTime(createForm.getEndTime());
        toUpdate.setProject(projectService.findProjectByProjectName(createForm.getProjectName()));
    }
}
