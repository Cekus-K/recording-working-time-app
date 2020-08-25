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
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    @Mock
    private ProjectViewRepository projectViewRepository;

    private UserRepository userRepository = new UserRepositoryStub();
    private ProjectRepository projectRepository = new ProjectRepositoryStub();

    private UserMapper userMapper = new UserMapperImpl();
    private ProjectMapper projectMapper = new ProjectMapperImpl();

    private UserService userService = new UserService(userRepository, userMapper);
    private ProjectService projectService = new ProjectService(projectRepository, projectViewRepository, userService, projectMapper);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateProjectWhenTheProjectNameDoesNotExistsYet() {
        //given
        ProjectCreateForm createForm = new ProjectCreateForm("project1", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(90), BigDecimal.valueOf(1000));

        //when
        int size = projectRepository.findAll().size();
        projectService.createProject(createForm);

        //then
        assertEquals(size + 1, projectRepository.findAll().size());
    }

    @Test
    void shouldThrowExceptionWhenTheProjectNameAlreadyExists() {
        //given
        ProjectCreateForm createForm = new ProjectCreateForm("project1", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(90), BigDecimal.valueOf(1000));

        //when
        projectService.createProject(createForm);

        //then
        assertThrows(NotUniqueException.class, () -> projectService.createProject(createForm));
    }

    @Test
    void shouldAddUserToTheProjectWhenTheUserIsAnEmployee() {
        //given
        UserDto user = userService.createUser(new UserCreateForm("user1", "firstName", "lastName",
                Role.EMPLOYEE, "123456", "test@email.com", BigDecimal.valueOf(10.0)));
        ProjectDto project = projectService.createProject(new ProjectCreateForm("project3", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1000)));

        //when
        int size = projectService.findProjectByProjectName(project.getProjectName()).getUsers().size();
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());

        //then
        assertEquals(size + 1, projectService.findProjectByProjectName(project.getProjectName()).getUsers().size());
    }

    @Test
    void shouldNotAddUserToTheProjectWhenTheUserIsAlreadyExistsInTheProject() {
        //given
        UserDto user = userService.createUser(new UserCreateForm("user1", "firstName", "lastName",
                Role.EMPLOYEE, "123456", "test@email.com", BigDecimal.valueOf(10.0)));
        ProjectDto project = projectService.createProject(new ProjectCreateForm("project3", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1000)));

        //when
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());
        int size = projectService.findProjectByProjectName(project.getProjectName()).getUsers().size();
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());

        //then
        assertEquals(size, projectService.findProjectByProjectName(project.getProjectName()).getUsers().size());
    }

    @Test
    void shouldThrowExceptionWhenTheUserAddedToTheProjectIsNotAnEmployee() {
        //given
        UserDto user = userService.createUser(new UserCreateForm("user1", "firstName", "lastName",
                Role.ADMIN, "123456", "test@email.com", BigDecimal.valueOf(10.0)));
        ProjectDto project = projectService.createProject(new ProjectCreateForm("project3", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1000)));

        //when + then
        assertThrows(IllegalParameterException.class,
                () -> projectService.addEmployeeToProject(project.getProjectName(), user.getLogin()));
    }

    @Test
    void shouldThrowExceptionWhenAddedProjectWithTheEnteredNameDoesNotExists() {
        //given
        UserDto user = userService.createUser(new UserCreateForm("user1", "firstName", "lastName",
                Role.ADMIN, "123456", "test@email.com", BigDecimal.valueOf(10.0)));
        String projectName = "non-existing project";

        //when + then
        assertThrows(NotFoundException.class,
                () -> projectService.addEmployeeToProject(projectName, user.getLogin()));
    }

    @Test
    void shouldRemoveTheUserFromTheProjectWhenTheUserExistsInTheProject() {
        //given
        UserDto user = userService.createUser(new UserCreateForm("user1", "firstName", "lastName",
                Role.EMPLOYEE, "123456", "test@email.com", BigDecimal.valueOf(10.0)));
        ProjectDto project = projectService.createProject(new ProjectCreateForm("project3", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1000)));

        //when
        projectService.addEmployeeToProject(project.getProjectName(), user.getLogin());
        int size = projectService.findProjectByProjectName(project.getProjectName()).getUsers().size();
        projectService.removeEmployeeFromProject(user.getLogin(), project.getProjectName());

        //then
        assertEquals(size - 1, projectService.findProjectByProjectName(project.getProjectName()).getUsers().size());
    }

    @Test
    void shouldNotRemoveTheUserFromTheProjectWhenTheUserIsNotExistsInTheProject() {
        //given
        UserDto user = userService.createUser(new UserCreateForm("user1", "firstName", "lastName",
                Role.EMPLOYEE, "123456", "test@email.com", BigDecimal.valueOf(10.0)));
        ProjectDto project = projectService.createProject(new ProjectCreateForm("project3", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1000)));

        //when
        int size = projectService.findProjectByProjectName(project.getProjectName()).getUsers().size();
        projectService.removeEmployeeFromProject(user.getLogin(), project.getProjectName());

        //then
        assertEquals(size, projectService.findProjectByProjectName(project.getProjectName()).getUsers().size());
    }

    @Test
    void shouldThrowExceptionWhenTheUserRemovedFromTheProjectIsNotAnEmployee() {
        //given
        UserDto user = userService.createUser(new UserCreateForm("user1", "firstName", "lastName",
                Role.ADMIN, "123456", "test@email.com", BigDecimal.valueOf(10.0)));
        ProjectDto project = projectService.createProject(new ProjectCreateForm("project3", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1000)));

        //when + then
        assertThrows(IllegalParameterException.class,
                () -> projectService.removeEmployeeFromProject(user.getLogin(), project.getProjectName()));
    }

    @Test
    void shouldReturnProjectWhenProjectWithUuidEnteredExists() {
        //given
        ProjectDto project = projectService.createProject(new ProjectCreateForm("project3", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1000)));

        //when
        Optional<Project> foundedProject = projectService.readProjectByUuid(project.getUuid());

        //then
        assertTrue(foundedProject.isPresent());
    }

    @Test
    void shouldNotReturnProjectWhenProjectWithUuidEnteredDoesNotExists() {
        //given
        ProjectDto project = projectService.createProject(new ProjectCreateForm("project3", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1000)));

        //when
        Optional<Project> foundedProject = projectService.readProjectByUuid(UUID.randomUUID());

        //then
        assertTrue(foundedProject.isEmpty());
    }

    @Test
    void readProjectsWithFilters() {
    }

    @Test
    void shouldUpdateProjectWhenProjectWithEnteredUuidExists() {
        //given
        ProjectDto project = projectService.createProject(new ProjectCreateForm("project3", "test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1000)));

        //when
        ProjectCreateForm toUpdate = new ProjectCreateForm("project2", "new test desc",
                LocalDate.now(), LocalDate.now().plusDays(30), BigDecimal.valueOf(1500));
        projectService.updateProject(project.getUuid(), toUpdate);
        Project updatedProject = projectRepository.findByUuid(project.getUuid()).orElseThrow(() -> new NotFoundException("not found"));

        //then
        assertAll(
                () -> assertEquals(updatedProject.getProjectName(), toUpdate.getProjectName()),
                () -> assertEquals(updatedProject.getDescription(), toUpdate.getDescription()),
                () -> assertEquals(updatedProject.getBudget(), toUpdate.getBudget()),
                () -> assertNotEquals(project.getProjectName(), toUpdate.getProjectName()),
                () -> assertNotEquals(project.getDescription(), toUpdate.getDescription()),
                () -> assertNotEquals(project.getBudget(), toUpdate.getBudget())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatedProjectWithTheEnteredUuidDoesNotExists() {
    }

    @Test
    void shouldThrowExceptionWhenTheProjectNameOfTheProjectBeingUpdatedAlreadyExists() {
    }

    @Test
    void deleteProject() {
    }

    @Test
    void getProjectReport() {
    }

    @Test
    void findProjectByProjectName() {
    }

    private List<Project> projects = new ArrayList<>();
}
