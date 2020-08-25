package pl.cekus.antologicproject.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.cekus.antologicproject.dto.ProjectDto;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.exception.IllegalParameterException;
import pl.cekus.antologicproject.exception.NotFoundException;
import pl.cekus.antologicproject.exception.NotUniqueException;
import pl.cekus.antologicproject.form.ProjectCreateForm;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.mapper.ProjectMapper;
import pl.cekus.antologicproject.mapper.ProjectMapperImpl;
import pl.cekus.antologicproject.mapper.UserMapper;
import pl.cekus.antologicproject.mapper.UserMapperImpl;
import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.ProjectView;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ProjectServiceTest {


    private UserMapper userMapper = new UserMapperImpl();
    private ProjectMapper projectMapper = new ProjectMapperImpl();

    private UserRepository userRepositoryMock = mock(UserRepository.class);
    private ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
    private ProjectViewRepository projectViewRepositoryMock = mock(ProjectViewRepository.class);
    private UserService userServiceMock = new UserService(userRepositoryMock, userMapper);
    private ProjectService projectServiceMock = new ProjectService(projectRepositoryMock, projectViewRepositoryMock, userServiceMock, projectMapper);

    private UserRepository userRepository = new UserRepositoryStub();
    private ProjectRepository projectRepository = new ProjectRepositoryStub();
    private UserService userService = new UserService(userRepository, userMapper);
    private ProjectService projectService = new ProjectService(projectRepository, projectViewRepositoryMock, userService, projectMapper);

    private ProjectCreateForm projectForm;
    private ProjectCreateForm projectUpdateForm;
    private UserCreateForm userForm;

    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        projectForm = new ProjectCreateForm("project1", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(90), BigDecimal.valueOf(1000));
        projectUpdateForm = new ProjectCreateForm("project2", "new test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1500));
        userForm = new UserCreateForm("user1", "firstName", "lastName",
                Role.EMPLOYEE, "123456", "test@email.com", BigDecimal.valueOf(10.0));

        user = new User("user1", "firstName", "lastName",
                Role.EMPLOYEE, "123456", "test@email.com", BigDecimal.valueOf(10.0));
        project = new Project("project1", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(90), BigDecimal.valueOf(1000));
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateProjectWhenTheProjectNameDoesNotExistsYet() {
        //given
        int size = projectRepository.findAll().size();

        //when
        projectService.createProject(projectForm);

        //then
        assertThat(projectRepository.findAll().size()).isEqualTo(size + 1);
    }

    @Test
    void shouldCreateProjectWhenTheProjectNameDoesNotExistsYetMockVersion() {
        //given
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);

        //when
        projectServiceMock.createProject(projectForm);

        //then
        verify(projectRepositoryMock, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTheProjectNameAlreadyExists() {
        //given
        projectService.createProject(projectForm);

        //when + then
        assertThrows(NotUniqueException.class, () -> projectService.createProject(projectForm));
    }

    @Test
    void shouldThrowExceptionWhenTheProjectNameAlreadyExistsMockVersion() {
        //given
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(true);

        //when + then
        assertThrows(NotUniqueException.class, () -> projectServiceMock.createProject(projectForm));
    }

    @Test
    void shouldAddUserToTheProjectWhenTheUserIsAnEmployee() {
        //given
        UserDto user = userService.createUser(userForm);
        ProjectDto project = projectService.createProject(projectForm);
        int size = projectService.findProjectByProjectName(project.getProjectName()).getUsers().size();

        //when
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());

        //then
        assertThat(projectService.findProjectByProjectName(project.getProjectName()).getUsers().size()).isEqualTo(size + 1);
    }

    @Test
    void shouldAddUserToTheProjectWhenTheUserIsAnEmployeeMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        given(userRepositoryMock.findByLogin(any())).willReturn(Optional.ofNullable(user));
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        given(projectRepositoryMock.findByProjectName(anyString())).willReturn(Optional.ofNullable(project));
        UserDto user = userServiceMock.createUser(userForm);
        ProjectDto project = projectServiceMock.createProject(projectForm);
        int size = projectServiceMock.findProjectByProjectName(project.getProjectName()).getUsers().size();

        //when
        projectServiceMock.addEmployeeToProject(project.getProjectName(), user.getLogin());

        //then
        assertThat(projectServiceMock.findProjectByProjectName(project.getProjectName()).getUsers().size()).isEqualTo(size + 1);
        verify(userRepositoryMock, times(1)).save(any());
        verify(projectRepositoryMock, times(2)).save(any());
    }

    @Test
    void shouldNotAddUserToTheProjectWhenTheUserIsAlreadyExistsInTheProject() {
        //given
        UserDto user = userService.createUser(userForm);
        ProjectDto project = projectService.createProject(projectForm);
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());
        int size = projectService.findProjectByProjectName(project.getProjectName()).getUsers().size();

        //when
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());

        //then
        assertThat(projectService.findProjectByProjectName(project.getProjectName()).getUsers().size()).isEqualTo(size);
    }

    @Test
    void shouldNotAddUserToTheProjectWhenTheUserIsAlreadyExistsInTheProjectMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        given(userRepositoryMock.findByLogin(any())).willReturn(Optional.ofNullable(user));
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        given(projectRepositoryMock.findByProjectName(anyString())).willReturn(Optional.ofNullable(project));
        UserDto user = userServiceMock.createUser(userForm);
        ProjectDto project = projectServiceMock.createProject(projectForm);
        projectServiceMock.addEmployeeToProject(project.getProjectName(), user.getLogin());
        int size = projectServiceMock.findProjectByProjectName(project.getProjectName()).getUsers().size();

        //when
        projectServiceMock.addEmployeeToProject(project.getProjectName(), user.getLogin());

        //then
        assertThat(projectServiceMock.findProjectByProjectName(project.getProjectName()).getUsers().size()).isEqualTo(size);
        verify(userRepositoryMock, times(1)).save(any());
        verify(projectRepositoryMock, times(3)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTheUserAddedToTheProjectIsNotAnEmployee() {
        //given
        UserCreateForm employee = this.userForm;
        employee.setRole(Role.ADMIN);
        UserDto user = userService.createUser(employee);
        ProjectDto project = projectService.createProject(projectForm);

        //when + then
        assertThrows(IllegalParameterException.class, () -> projectService.addEmployeeToProject(project.getProjectName(), user.getLogin()));
    }

    @Test
    void shouldThrowExceptionWhenTheUserAddedToTheProjectIsNotAnEmployeeMockVersion() {
        //given
        User user = this.user;
        user.setRole(Role.ADMIN);
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        given(userRepositoryMock.findByLogin(any())).willReturn(Optional.of(user));
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        given(projectRepositoryMock.findByProjectName(any())).willReturn(Optional.ofNullable(project));
        UserDto userDto = userServiceMock.createUser(userForm);
        ProjectDto project = projectServiceMock.createProject(projectForm);

        //when + then
        assertThrows(IllegalParameterException.class, () -> projectServiceMock.addEmployeeToProject(project.getProjectName(), userDto.getLogin()));
    }

    @Test
    void shouldThrowExceptionWhenWhenAddedUserToProjectWithTheEnteredNameDoesNotExists() {
        //given
        UserDto user = userService.createUser(userForm);
        String projectName = "non-existing project";

        //when + then
        assertThrows(NotFoundException.class, () -> projectService.addEmployeeToProject(projectName, user.getLogin()));
    }

    @Test
    void shouldThrowExceptionWhenWhenAddedUserToProjectWithTheEnteredNameDoesNotExistsMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        UserDto user = userServiceMock.createUser(userForm);
        String projectName = "non-existing project";

        //when + then
        assertThrows(NotFoundException.class, () -> projectServiceMock.addEmployeeToProject(projectName, user.getLogin()));
    }

    @Test
    void shouldRemoveTheUserFromTheProjectWhenTheUserExistsInTheProject() {
        //given
        UserDto user = userService.createUser(userForm);
        ProjectDto project = projectService.createProject(projectForm);
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());
        int size = projectService.findProjectByProjectName(project.getProjectName()).getUsers().size();

        //when
        projectService.removeEmployeeFromProject(user.getLogin(), project.getProjectName());

        //then
        assertThat(projectService.findProjectByProjectName(project.getProjectName()).getUsers().size()).isEqualTo(size - 1);
    }

    @Test
    void shouldRemoveTheUserFromTheProjectWhenTheUserExistsInTheProjectMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        given(userRepositoryMock.findByLogin(any())).willReturn(Optional.of(user));
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        given(projectRepositoryMock.findByProjectName(any())).willReturn(Optional.ofNullable(project));
        UserDto user = userServiceMock.createUser(userForm);
        ProjectDto project = projectServiceMock.createProject(projectForm);
        projectServiceMock.addEmployeeToProject(project.getProjectName(), user.getLogin());
        int size = projectServiceMock.findProjectByProjectName(project.getProjectName()).getUsers().size();

        //when
        projectServiceMock.removeEmployeeFromProject(user.getLogin(), project.getProjectName());

        //then
        assertThat(projectServiceMock.findProjectByProjectName(project.getProjectName()).getUsers().size()).isEqualTo(size - 1);
    }

    @Test
    void shouldNotRemoveTheUserFromTheProjectWhenTheUserIsNotExistsInTheProject() {
        //given
        UserDto user = userService.createUser(userForm);
        ProjectDto project = projectService.createProject(projectForm);
        int size = projectService.findProjectByProjectName(project.getProjectName()).getUsers().size();

        //when
        projectService.removeEmployeeFromProject(user.getLogin(), project.getProjectName());

        //then
        assertThat(projectService.findProjectByProjectName(project.getProjectName()).getUsers().size()).isEqualTo(size);
    }

    @Test
    void shouldNotRemoveTheUserFromTheProjectWhenTheUserIsNotExistsInTheProjectMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        given(userRepositoryMock.findByLogin(any())).willReturn(Optional.of(user));
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        given(projectRepositoryMock.findByProjectName(any())).willReturn(Optional.ofNullable(project));
        UserDto user = userServiceMock.createUser(userForm);
        ProjectDto project = projectServiceMock.createProject(projectForm);
        int size = projectServiceMock.findProjectByProjectName(project.getProjectName()).getUsers().size();

        //when
        projectServiceMock.removeEmployeeFromProject(user.getLogin(), project.getProjectName());

        //then
        assertThat(projectServiceMock.findProjectByProjectName(project.getProjectName()).getUsers().size()).isEqualTo(size);
    }

    @Test
    void shouldThrowExceptionWhenTheUserRemovedFromTheProjectIsNotAnEmployee() {
        //given
        UserCreateForm employee = this.userForm;
        employee.setRole(Role.ADMIN);
        UserDto user = userService.createUser(employee);
        ProjectDto project = projectService.createProject(projectForm);

        //when + then
        assertThrows(IllegalParameterException.class, () -> projectService.removeEmployeeFromProject(user.getLogin(), project.getProjectName()));
    }

    @Test
    void shouldThrowExceptionWhenTheUserRemovedFromTheProjectIsNotAnEmployeeMockVersion() {
        //given
        User user = this.user;
        user.setRole(Role.ADMIN);
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        given(userRepositoryMock.findByLogin(any())).willReturn(Optional.of(user));
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        given(projectRepositoryMock.findByProjectName(any())).willReturn(Optional.ofNullable(project));
        UserDto userDto = userServiceMock.createUser(userForm);
        ProjectDto project = projectServiceMock.createProject(projectForm);

        //when + then
        assertThrows(IllegalParameterException.class, () -> projectServiceMock.removeEmployeeFromProject(userDto.getLogin(), project.getProjectName()));
    }

    @Test
    void shouldReturnProjectWhenProjectWithUuidEnteredExists() {
        //given
        ProjectDto project = projectService.createProject(projectForm);

        //when
        Optional<Project> foundedProject = projectService.readProjectByUuid(project.getUuid());

        //then
        assertThat(foundedProject.isPresent()).isTrue();
    }

    @Test
    void shouldReturnProjectWhenProjectWithUuidEnteredExistsMockVersion() {
        //given
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        given(projectRepositoryMock.findByUuid(any())).willReturn(Optional.ofNullable(project));
        ProjectDto project = projectServiceMock.createProject(projectForm);

        //when
        Optional<Project> foundedProject = projectServiceMock.readProjectByUuid(project.getUuid());

        //then
        assertThat(foundedProject.isPresent()).isTrue();
    }

    @Test
    void shouldNotReturnProjectWhenProjectWithUuidEnteredDoesNotExists() {
        //when
        Optional<Project> foundedProject = projectService.readProjectByUuid(UUID.randomUUID());

        //then
        assertThat(foundedProject.isEmpty()).isTrue();
    }

    @Test
    void shouldNotReturnProjectWhenProjectWithUuidEnteredDoesNotExistsMockVersion() {
        //when
        Optional<Project> foundedProject = projectServiceMock.readProjectByUuid(UUID.randomUUID());

        //then
        assertThat(foundedProject.isEmpty()).isTrue();
    }

    @Test
    void shouldUpdateProjectWhenProjectWithEnteredUuidExists() {
        //given
        ProjectDto project = projectService.createProject(projectForm);

        //when
        projectService.updateProject(project.getUuid(), projectUpdateForm);
        Project updatedProject = projectRepository.findByUuid(project.getUuid()).orElseThrow();

        //then
        assertAll(
                () -> assertThat(updatedProject.getProjectName()).isEqualTo(projectUpdateForm.getProjectName()),
                () -> assertThat(updatedProject.getDescription()).isEqualTo(projectUpdateForm.getDescription()),
                () -> assertThat(updatedProject.getBudget()).isEqualTo(projectUpdateForm.getBudget()),
                () -> assertThat(project.getProjectName()).isNotEqualTo(updatedProject.getProjectName()),
                () -> assertThat(project.getDescription()).isNotEqualTo(updatedProject.getDescription()),
                () -> assertThat(project.getBudget()).isNotEqualTo(updatedProject.getBudget())
        );
    }

    @Test
    void shouldUpdateProjectWhenProjectWithEnteredUuidExistsMockVersion() {
        //given
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        given(projectRepositoryMock.findByUuid(any())).willReturn(Optional.ofNullable(project));
        ProjectDto project = projectServiceMock.createProject(projectForm);

        //when
        projectServiceMock.updateProject(project.getUuid(), projectUpdateForm);
        Project updatedProject = projectRepositoryMock.findByUuid(project.getUuid()).orElseThrow();

        //then
        assertAll(
                () -> assertThat(updatedProject.getProjectName()).isEqualTo(projectUpdateForm.getProjectName()),
                () -> assertThat(updatedProject.getDescription()).isEqualTo(projectUpdateForm.getDescription()),
                () -> assertThat(updatedProject.getBudget()).isEqualTo(projectUpdateForm.getBudget()),
                () -> assertThat(project.getProjectName()).isNotEqualTo(updatedProject.getProjectName()),
                () -> assertThat(project.getDescription()).isNotEqualTo(updatedProject.getDescription()),
                () -> assertThat(project.getBudget()).isNotEqualTo(updatedProject.getBudget())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatedProjectWithTheEnteredUuidDoesNotExists() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> projectService.updateProject(UUID.randomUUID(), projectUpdateForm));
    }

    @Test
    void shouldThrowExceptionWhenUpdatedProjectWithTheEnteredUuidDoesNotExistsMockVersion() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> projectServiceMock.updateProject(UUID.randomUUID(), projectUpdateForm));
    }

    @Test
    void shouldThrowExceptionWhenTheProjectNameOfTheProjectBeingUpdatedAlreadyExists() {
        //given
        ProjectDto project = projectService.createProject(projectForm);
        projectService.createProject(projectUpdateForm);

        //then + then
        assertThrows(NotUniqueException.class, () -> projectService.updateProject(project.getUuid(), projectUpdateForm));
    }

    @Test
    void shouldThrowExceptionWhenTheProjectNameOfTheProjectBeingUpdatedAlreadyExistsMockVersion() {
        //given
        given(projectRepositoryMock.findByUuid(any())).willReturn(Optional.ofNullable(project));
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(true);

        //then + then
        assertThrows(NotUniqueException.class, () -> projectServiceMock.updateProject(project.getUuid(), projectUpdateForm));
    }

    @Test
    void shouldNotThrowExceptionWhenTheProjectNameOfTheProjectBeingUpdatedIsUpdatedToTheSameProjectName() {
        //given
        ProjectDto project = projectService.createProject(projectUpdateForm);
        Project afterUpdate = projectService.readProjectByUuid(project.getUuid()).orElseThrow();

        //when
        projectService.updateProject(project.getUuid(), projectUpdateForm);

        //then
        assertThat(projectUpdateForm.getBudget()).isEqualTo(afterUpdate.getBudget());
    }

    @Test
    void shouldNotThrowExceptionWhenTheProjectNameOfTheProjectBeingUpdatedIsUpdatedToTheSameProjectNameMockVersion() {
        //given
        given(projectRepositoryMock.findByUuid(any())).willReturn(Optional.ofNullable(project));
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        ProjectDto project = projectServiceMock.createProject(projectUpdateForm);
        Project afterUpdate = projectServiceMock.readProjectByUuid(project.getUuid()).orElseThrow();

        //when
        projectServiceMock.updateProject(project.getUuid(), projectUpdateForm);

        //then
        assertThat(projectUpdateForm.getBudget()).isEqualTo(afterUpdate.getBudget());
    }

    @Test
    void shouldThrowExceptionWhenTheProjectBeingDeletedWithGivenUuidDoesNotExists() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> projectService.deleteProject(UUID.randomUUID()));
    }

    @Test
    void shouldThrowExceptionWhenTheProjectBeingDeletedWithGivenUuidDoesNotExistsMockVersion() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> projectServiceMock.deleteProject(UUID.randomUUID()));
    }

    @Test
    void shouldRemoveAllUsersFromTheProjectBeingDeleted() {
        //given
        ProjectDto project = projectService.createProject(projectForm);
        UserDto user = userService.createUser(this.userForm);
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());
        int size = userService.findUserByLogin(user.getLogin()).getProjects().size();

        //when
        projectService.deleteProject(project.getUuid());

        //then
        assertThat(userService.findUserByLogin(user.getLogin()).getProjects().size()).isEqualTo(size - 1);
    }

    @Test
    void shouldRemoveAllUsersFromTheProjectBeingDeletedMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        given(userRepositoryMock.findByLogin(any())).willReturn(Optional.of(user));
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.save(any())).willReturn(project);
        given(projectRepositoryMock.findByProjectName(any())).willReturn(Optional.ofNullable(project));
        given(projectRepositoryMock.findByUuid(any())).willReturn(Optional.ofNullable(project));
        ProjectDto project = projectServiceMock.createProject(projectForm);
        UserDto user = userServiceMock.createUser(userForm);
        projectServiceMock.addEmployeeToProject(project.getProjectName(), user.getLogin());
        int size = userServiceMock.findUserByLogin(user.getLogin()).getProjects().size();

        //when
        projectServiceMock.deleteProject(project.getUuid());

        //then
        assertThat(userServiceMock.findUserByLogin(user.getLogin()).getProjects().size()).isEqualTo(size - 1);
    }

    @Test
    void shouldDeleteProjectWhenProjectWithEnteredUuidExists() {
        //given
        ProjectDto project = projectService.createProject(projectForm);
        int size = projectRepository.findAll().size();

        //when
        projectService.deleteProject(project.getUuid());

        //then
        assertThat(projectRepository.findAll().size()).isEqualTo(size - 1);
    }

    @Test
    void shouldDeleteProjectWhenProjectWithEnteredUuidExistsMockVersion() {
        //given
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.findByUuid(any())).willReturn(Optional.ofNullable(project));
        given(projectRepositoryMock.save(any())).willReturn(project);
        ProjectDto project = projectServiceMock.createProject(projectForm);

        //when
        projectServiceMock.deleteProject(project.getUuid());

        //then
        verify(projectRepositoryMock, times(1)).deleteByUuid(any());
    }

    @Test
    void shouldReturnProjectWhenProjectWithGivenProjectNameExists() {
        //given
        ProjectDto project = projectService.createProject(projectForm);

        //when
        Project result = projectService.findProjectByProjectName(project.getProjectName());

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void shouldReturnProjectWhenProjectWithGivenProjectNameExistsMockVersion() {
        //given
        given(projectRepositoryMock.existsByProjectName(anyString())).willReturn(false);
        given(projectRepositoryMock.findByProjectName(anyString())).willReturn(Optional.ofNullable(project));
        given(projectRepositoryMock.save(any())).willReturn(project);
        ProjectDto project = projectServiceMock.createProject(projectForm);

        //when
        Project result = projectServiceMock.findProjectByProjectName(project.getProjectName());

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenProjectWithGivenProjectNameDoesNotExists() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> projectService.findProjectByProjectName("non-existing"));
    }

    @Test
    void shouldThrowExceptionWhenProjectWithGivenProjectNameDoesNotExistsMockVersion() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> projectServiceMock.findProjectByProjectName("non-existing"));
    }

    @Test
    void getProjectReport() {
    }

    @Test
    void readProjectsWithFilters() {
    }
}
