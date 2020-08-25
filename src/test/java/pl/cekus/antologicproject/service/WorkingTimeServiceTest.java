package pl.cekus.antologicproject.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.dto.WorkingTimeDto;
import pl.cekus.antologicproject.exception.NotFoundException;
import pl.cekus.antologicproject.form.ProjectCreateForm;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.form.WorkingTimeCreateForm;
import pl.cekus.antologicproject.mapper.*;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.WorkingTime;
import pl.cekus.antologicproject.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkingTimeServiceTest {

    @Mock
    private ProjectViewRepository projectViewRepository;

    private UserMapper userMapper = new UserMapperImpl();
    private ProjectMapper projectMapper = new ProjectMapperImpl();
    private WorkingTimeMapper workingTimeMapper = new WorkingTimeMapperImpl();

    private UserRepository userRepository = new UserRepositoryStub();
    private ProjectRepository projectRepository = new ProjectRepositoryStub();
    private WorkingTimeRepository workingTimeRepository = new WorkingTimeRepositoryStub();

    private UserService userService = new UserService(userRepository, userMapper);
    private ProjectService projectService = new ProjectService(projectRepository, projectViewRepository, userService, projectMapper);
    private WorkingTimeService workingTimeService = new WorkingTimeService(workingTimeRepository, userService, projectService, workingTimeMapper);

    private ProjectCreateForm project;
    private UserCreateForm user;
    private WorkingTimeCreateForm createForm;
    private WorkingTimeCreateForm updateForm;

    @BeforeEach
    void setUp() {
        project = new ProjectCreateForm("project", "test desc",
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(10), BigDecimal.valueOf(5000));
        user = new UserCreateForm("user", "test", "test",
                Role.EMPLOYEE, "123", "test@email.com", BigDecimal.valueOf(10.0));
        createForm = new WorkingTimeCreateForm(LocalDateTime.now().minusHours(5), LocalDateTime.now(),
                user.getLogin(), project.getProjectName());
        updateForm = new WorkingTimeCreateForm(LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(5),
                user.getLogin(), project.getProjectName());
    }

    @AfterEach
    void tearDown() {
        workingTimeRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldAddWorkingTimeToUserWhenUserIsInProject() {
        //given
        UserDto user = userService.createUser(this.user);
        ProjectDto project = projectService.createProject(this.project);
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());
        int workingTimeSize = workingTimeRepository.findAll().size();
        int userWorkingTimeSize = userRepository.findByUuid(user.getUuid()).orElseThrow().getWorkingTimes().size();
        int projectWorkingTimeSize = projectRepository.findByUuid(project.getUuid()).orElseThrow().getWorkingTimes().size();

        //when
        workingTimeService.addWorkingTimeToUser(createForm);

        //then
        assertAll(
                () -> assertThat(workingTimeRepository.findAll().size()).isEqualTo(workingTimeSize + 1),
                () -> assertThat(userRepository.findByUuid(user.getUuid()).orElseThrow().getWorkingTimes().size()).isEqualTo(userWorkingTimeSize + 1),
                () -> assertThat(projectRepository.findByUuid(project.getUuid()).orElseThrow().getWorkingTimes().size()).isEqualTo(projectWorkingTimeSize + 1)
        );
    }

    @Test
    void shouldThrowExceptionWhenAddingWorkingTimeToAUserWhoDoesNotExistsInTheProject() {
        //given
        userService.createUser(user);
        projectService.createProject(project);

        //when + then
        assertThrows(NotFoundException.class, () -> workingTimeService.addWorkingTimeToUser(createForm));
    }

    @Test
    void shouldReadAllWorkingTimesFromUser() {
        //given
        UserDto userDto = userService.createUser(user);
        List<WorkingTimeDto> userWorkingTimes = userService.findUserByLogin(user.getLogin()).getWorkingTimes().stream()
                .map(workingTimeMapper::mapWorkingTimeToWorkingTimeDto)
                .collect(Collectors.toList());

        //when + then
        assertThat(workingTimeService.readAllWorkingTimes(userDto.getLogin()).size()).isEqualTo(userWorkingTimes.size());
    }

    @Test
    void shouldUpdateWorkingTimeWhenWorkingTimeWithEnteredUuidExists() {
        //given
        UserDto user = userService.createUser(this.user);
        ProjectDto project = projectService.createProject(this.project);
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());
        WorkingTimeDto workingTime = workingTimeService.addWorkingTimeToUser(createForm);

        //when
        workingTimeService.updateWorkingTime(workingTime.getUuid(), updateForm);
        WorkingTime updatedWorkingTime = workingTimeRepository.findByUuid(workingTime.getUuid()).orElseThrow();

        //then
        assertAll(
                () -> Assertions.assertThat(updatedWorkingTime.getStartTime()).isEqualTo(updateForm.getStartTime()),
                () -> Assertions.assertThat(updatedWorkingTime.getEndTime()).isEqualTo(updateForm.getEndTime()),
                () -> Assertions.assertThat(workingTime.getStartTime()).isNotEqualTo(updatedWorkingTime.getStartTime()),
                () -> Assertions.assertThat(workingTime.getEndTime()).isNotEqualTo(updatedWorkingTime.getEndTime())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatedWorkingTimeWithTheEnteredUuidDoesNotExists() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> workingTimeService.updateWorkingTime(UUID.randomUUID(), createForm));
    }

    @Test
    void shouldDeleteWorkingTimeWhenWorkingTimeWithEnteredUuidExists() {
        //given
        UserDto user = userService.createUser(this.user);
        ProjectDto project = projectService.createProject(this.project);
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());
        WorkingTimeDto workingTimeDto = workingTimeService.addWorkingTimeToUser(createForm);
        int workingTimeSize = workingTimeRepository.findAll().size();
        int userWorkingTimeSize = userRepository.findByUuid(user.getUuid()).orElseThrow().getWorkingTimes().size();
        int projectWorkingTimeSize = projectRepository.findByUuid(project.getUuid()).orElseThrow().getWorkingTimes().size();

        //when
        workingTimeService.deleteWorkingTime(workingTimeDto.getUuid());

        //then
        assertAll(
                () -> assertThat(workingTimeRepository.findAll().size()).isEqualTo(workingTimeSize - 1),
                () -> assertThat(userRepository.findByUuid(user.getUuid()).orElseThrow().getWorkingTimes().size()).isEqualTo(userWorkingTimeSize - 1),
                () -> assertThat(projectRepository.findByUuid(project.getUuid()).orElseThrow().getWorkingTimes().size()).isEqualTo(projectWorkingTimeSize - 1)
        );
    }

    @Test
    void shouldThrowExceptionWhenTheWorkingTimeBeingDeletedWithGivenUuidDoesNotExists() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> workingTimeService.deleteWorkingTime(UUID.randomUUID()));
    }
}
