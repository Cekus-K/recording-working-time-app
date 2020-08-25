package pl.cekus.antologicproject.service;

import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.WorkingTimeDto;
import pl.cekus.antologicproject.exception.NotFoundException;
import pl.cekus.antologicproject.form.WorkingTimeCreateForm;
import pl.cekus.antologicproject.mapper.WorkingTimeMapper;
import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.model.WorkingTime;
import pl.cekus.antologicproject.repository.WorkingTimeRepository;

import java.util.List;
import java.util.UUID;
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
    public WorkingTimeDto addWorkingTimeToUser(WorkingTimeCreateForm createForm) {
        User user = userService.findUserByLogin(createForm.getUserLogin());
        Project project = projectService.findProjectByProjectName(createForm.getProjectName());
        if (!user.getProjects().contains(project)) {
            throw new NotFoundException("the given employee is not assigned to given project");
        }
        WorkingTime workingTime = new WorkingTime(createForm.getStartTime(), createForm.getEndTime(), user, project);
        user.addWorkingTime(workingTime);
        project.getWorkingTimes().add(workingTime);
        workingTimeRepository.save(workingTime);
        return workingTimeMapper.mapWorkingTimeToWorkingTimeDto(workingTime);
    }

    public List<WorkingTimeDto> readAllWorkingTimes(String login) {
        User user = userService.findUserByLogin(login);
        return user.getWorkingTimes().stream()
                .map(workingTimeMapper::mapWorkingTimeToWorkingTimeDto)
                .collect(Collectors.toList());
    }

    // TODO: validation of time period overlapping
    public void updateWorkingTime(UUID uuid, WorkingTimeCreateForm workingTimeCreateForm) {
        WorkingTime toUpdate = workingTimeRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("provided working time uuid not found"));
        workingTimeMapper.fromWorkingTimeCreateFormToWorkingTime(workingTimeCreateForm, toUpdate);
        workingTimeRepository.save(toUpdate);
    }

    public void deleteWorkingTime(UUID uuid) {
        WorkingTime toDelete = workingTimeRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("provided working time uuid not found"));
        User user = toDelete.getUser();
        toDelete.getProject().getWorkingTimes().remove(toDelete);
        user.removeWorkingTime(toDelete);
        workingTimeRepository.delete(toDelete);
    }
}
