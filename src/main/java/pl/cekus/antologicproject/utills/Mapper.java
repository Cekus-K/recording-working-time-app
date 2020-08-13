package pl.cekus.antologicproject.utills;

import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.dto.WorkingTimeDto;
import pl.cekus.antologicproject.form.ProjectCreateForm;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.model.WorkingTime;

import java.util.Set;
import java.util.stream.Collectors;

public final class Mapper {

    private Mapper() {
    }

    public static User mapCreateFormToUser(UserCreateForm createForm) {
        return new User(createForm.getLogin(), createForm.getFirstName(), createForm.getLastName(),
                mapStringToRole(createForm.getRole()), createForm.getPassword(), createForm.getEmail(), createForm.getCostPerHour());
    }

    public static UserDto mapUserToUserDto(User user) {
        return new UserDto(user.getLogin(), user.getFirstName(), user.getLastName(),
                user.getRole(), user.getEmail(), user.getCostPerHour());
    }

    public static Role mapStringToRole(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    public static Project mapCreateFormToProject(ProjectCreateForm createForm) {
        return new Project(createForm.getProjectName(), createForm.getDescription(), createForm.getStartDate(),
                createForm.getEndDate(), createForm.getBudget());
    }

    public static ProjectDto mapProjectToProjectDto(Project project) {
        return new ProjectDto(project.getProjectName(), project.getDescription(), project.getStartDate(),
                project.getEndDate(), project.getBudget(), mapSetUserToSetUserDto(project.getUsers()));
    }

    private static Set<UserDto> mapSetUserToSetUserDto(Set<User> userSet) {
        return userSet.stream()
                .map(Mapper::mapUserToUserDto)
                .collect(Collectors.toSet());
    }

    public static WorkingTimeDto mapWorkingTimeToWorkingTimeDto(WorkingTime workingTime) {
        return new WorkingTimeDto(workingTime.getStartTime(), workingTime.getEndTime(), workingTime.getProject().getProjectName());
    }
}
