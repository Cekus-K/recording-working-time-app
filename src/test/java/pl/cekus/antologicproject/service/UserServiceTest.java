package pl.cekus.antologicproject.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.exception.NotFoundException;
import pl.cekus.antologicproject.exception.NotUniqueException;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.mapper.UserMapper;
import pl.cekus.antologicproject.mapper.UserMapperImpl;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.repository.UserRepository;
import pl.cekus.antologicproject.repository.UserRepositoryStub;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserMapper userMapper = new UserMapperImpl();

    private UserRepository userRepository = new UserRepositoryStub();
    private UserRepository userRepositoryMock = mock(UserRepository.class);

    private UserService userServiceMock = new UserService(userRepositoryMock, userMapper);
    private UserService userService = new UserService(userRepository, userMapper);

    private UserCreateForm createForm;
    private UserCreateForm updateForm;

    private User user;

    @BeforeEach
    void setUp() {
        createForm = new UserCreateForm("user1", "firstName", "lastName",
                Role.EMPLOYEE, "123456", "test@email.com", BigDecimal.valueOf(10.0));
        updateForm = new UserCreateForm("user2", "firstName", "lastName2",
                Role.EMPLOYEE, "123456", "test@email.com", BigDecimal.valueOf(15.0));

        user = new User("user1", "firstName", "lastName",
                Role.EMPLOYEE, "123456", "test@email.com", BigDecimal.valueOf(10.0));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUserWhenTheUserLoginDoesNotExistsYet() {
        //given
        int size = userRepository.findAll().size();

        //when
        userService.createUser(createForm);

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(size + 1);
    }

    @Test
    void shouldCreateUserWhenTheUserLoginDoesNotExistsYetMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);

        //when
        userServiceMock.createUser(createForm);

        //then
        verify(userRepositoryMock, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTheUserLoginAlreadyExists() {
        //given
        userService.createUser(createForm);

        //when + then
        assertThrows(NotUniqueException.class, () -> userService.createUser(createForm));
    }

    @Test
    void shouldThrowExceptionWhenTheUserLoginAlreadyExistsMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(true);

        //when + then
        assertThrows(NotUniqueException.class, () -> userServiceMock.createUser(createForm));
    }

    @Test
    void shouldUpdateUserWhenUserWithEnteredUuidExists() {
        //given
        UserDto user = userService.createUser(createForm);

        //when
        userService.updateUser(user.getUuid(), updateForm);
        User updatedUser = userRepository.findByUuid(user.getUuid()).orElseThrow();

        //then
        assertAll(
                () -> assertThat(updatedUser.getLogin()).isEqualTo(updateForm.getLogin()),
                () -> assertThat(updatedUser.getLastName()).isEqualTo(updateForm.getLastName()),
                () -> assertThat(updatedUser.getCostPerHour()).isEqualTo(updateForm.getCostPerHour()),
                () -> assertThat(user.getLogin()).isNotEqualTo(updatedUser.getLogin()),
                () -> assertThat(user.getLastName()).isNotEqualTo(updatedUser.getLastName()),
                () -> assertThat(user.getCostPerHour()).isNotEqualTo(updatedUser.getCostPerHour())
        );
    }

    @Test
    void shouldUpdateUserWhenUserWithEnteredUuidExistsMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        given(userRepositoryMock.findByUuid(any())).willReturn(Optional.ofNullable(user));
        UserDto user = userServiceMock.createUser(createForm);

        //when
        userServiceMock.updateUser(user.getUuid(), updateForm);
        User updatedUser = userRepositoryMock.findByUuid(user.getUuid()).orElseThrow();

        //then
        assertAll(
                () -> assertThat(updatedUser.getLogin()).isEqualTo(updateForm.getLogin()),
                () -> assertThat(updatedUser.getLastName()).isEqualTo(updateForm.getLastName()),
                () -> assertThat(updatedUser.getCostPerHour()).isEqualTo(updateForm.getCostPerHour()),
                () -> assertThat(user.getLogin()).isNotEqualTo(updatedUser.getLogin()),
                () -> assertThat(user.getLastName()).isNotEqualTo(updatedUser.getLastName()),
                () -> assertThat(user.getCostPerHour()).isNotEqualTo(updatedUser.getCostPerHour())
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatedUserWithTheEnteredUuidDoesNotExists() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> userService.updateUser(UUID.randomUUID(), createForm));
    }

    @Test
    void shouldThrowExceptionWhenUpdatedUserWithTheEnteredUuidDoesNotExistsMockVersion() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> userServiceMock.updateUser(UUID.randomUUID(), createForm));
    }

    @Test
    void shouldThrowExceptionWhenTheLoginOfTheUserBeingUpdatedAlreadyExists() {
        //given
        UserDto user = userService.createUser(createForm);
        userService.createUser(updateForm);

        //when + then
        assertThrows(NotUniqueException.class, () -> userService.updateUser(user.getUuid(), updateForm));
    }

    @Test
    void shouldThrowExceptionWhenTheLoginOfTheUserBeingUpdatedAlreadyExistsMockVersion() {
        //given
        given(userRepositoryMock.findByUuid(any())).willReturn(Optional.ofNullable(user));
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(true);

        //when + then
        assertThrows(NotUniqueException.class, () -> userServiceMock.updateUser(user.getUuid(), updateForm));
    }

    @Test
    void shouldNotThrowExceptionWhenTheLoginOfTheUserBeingUpdatedIsUpdatedToTheSameLogin() {
        //given
        UserDto user = userService.createUser(createForm);
        User afterUpdate = userRepository.findByUuid(user.getUuid()).orElseThrow();

        //when
        userService.updateUser(user.getUuid(), updateForm);

        //then
        assertThat(updateForm.getCostPerHour()).isEqualTo(afterUpdate.getCostPerHour());
    }

    @Test
    void shouldNotThrowExceptionWhenTheLoginOfTheUserBeingUpdatedIsUpdatedToTheSameLoginMockVersion() {
        //given
        given(userRepositoryMock.findByUuid(any())).willReturn(Optional.ofNullable(user));
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        UserDto user = userServiceMock.createUser(createForm);
        User afterUpdate = userRepositoryMock.findByUuid(user.getUuid()).orElseThrow();

        //when
        userServiceMock.updateUser(user.getUuid(), updateForm);

        //then
        assertThat(updateForm.getCostPerHour()).isEqualTo(afterUpdate.getCostPerHour());
    }

    @Test
    void shouldDeleteUserWhenUserWithEnteredUuidExists() {
        //given
        UserDto user = userService.createUser(createForm);
        int size = userRepository.findAll().size();

        //when
        userService.deleteUser(user.getUuid());

        //then
        assertThat(userRepository.findAll().size()).isEqualTo(size - 1);
    }

    @Test
    void shouldDeleteUserWhenUserWithEnteredUuidExistsMockVersion() {
        //given
        given(userRepositoryMock.existsByUuid(any())).willReturn(true);
        given(userRepositoryMock.save(any())).willReturn(user);
        UserDto user = userServiceMock.createUser(createForm);

        //when
        userServiceMock.deleteUser(user.getUuid());

        //then
        verify(userRepositoryMock, times(1)).deleteByUuid(any());
    }

    @Test
    void shouldThrowExceptionWhenTheUserBeingDeletedWithGivenUuidDoesNotExists() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> userService.deleteUser(UUID.randomUUID()));
    }

    @Test
    void shouldThrowExceptionWhenTheUserBeingDeletedWithGivenUuidDoesNotExistsMockVersion() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> userServiceMock.deleteUser(UUID.randomUUID()));
    }

    @Test
    void shouldReturnUserWhenUserWithGivenLoginExists() {
        //given
        UserDto user = userService.createUser(createForm);

        //when
        User result = userService.findUserByLogin(user.getLogin());

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void shouldReturnUserWhenUserWithGivenLoginExistsMockVersion() {
        //given
        given(userRepositoryMock.existsByLogin(anyString())).willReturn(false);
        given(userRepositoryMock.save(any())).willReturn(user);
        given(userRepositoryMock.findByLogin(anyString())).willReturn(Optional.ofNullable(user));
        UserDto user = userServiceMock.createUser(createForm);

        //when
        User result = userServiceMock.findUserByLogin(user.getLogin());

        //then
        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenUserWithGivenLoginDoesNotExists() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> userService.findUserByLogin("non-existing"));
    }

    @Test
    void shouldThrowExceptionWhenUserWithGivenLoginDoesNotExistsMockVersion() {
        //given + when + then
        assertThrows(NotFoundException.class, () -> userServiceMock.findUserByLogin("non-existing"));
    }

    @Test
    void getUserReport() {
    }

    @Test
    void readUsersWithFilters() {
    }
}
