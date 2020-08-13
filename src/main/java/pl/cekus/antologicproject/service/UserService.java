package pl.cekus.antologicproject.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.form.UserFilterForm;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.repository.UserRepository;
import pl.cekus.antologicproject.specification.UserSpecification;
import pl.cekus.antologicproject.utills.Mapper;

import java.util.Optional;

import static pl.cekus.antologicproject.utills.Mapper.*;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserCreateForm userCreateForm) {
        if (!userRepository.existsByLogin(userCreateForm.getLogin())) {
            User toCreate = mapCreateFormToUser(userCreateForm);
            return mapUserToUserDto(userRepository.save(toCreate));
        }
        return mapUserToUserDto(findUserByLogin(userCreateForm.getLogin()));
    }

    public Optional<User> readUserById(Long id) {
        return userRepository.findById(id);
    }

    public Page<UserDto> readUsersWithFilters(UserFilterForm filterForm, Pageable pageable) {
        UserSpecification specification = new UserSpecification(filterForm);
        return userRepository.findAll(specification, pageable)
                .map(Mapper::mapUserToUserDto);
    }

    public void updateUser(Long id, UserCreateForm userCreateForm) {
        Optional<User> toUpdate = userRepository.findById(id);
        toUpdate.ifPresent(user -> {
            checkIfLoginAlreadyExists(user.getLogin());
            setValuesToUpdatingUser(user, userCreateForm);
            userRepository.save(user);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    User findUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new IllegalArgumentException("provided user not found"));
    }

    // FIXME: write own exceptions
    private void checkIfLoginAlreadyExists(String login) {
        if (findUserByLogin(login) != null) {
            throw new IllegalStateException("provided login already exists");
        }
    }

    private void setValuesToUpdatingUser(User toUpdate, UserCreateForm createForm) {
        toUpdate.setLogin(createForm.getLogin());
        toUpdate.setFirstName(createForm.getFirstName());
        toUpdate.setLastName(createForm.getLastName());
        toUpdate.setRole(mapStringToRole(createForm.getRole()));
        toUpdate.setPassword(createForm.getPassword());
        toUpdate.setEmail(createForm.getEmail());
        toUpdate.setCostPerHour(createForm.getCostPerHour());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createTestUser() {
        createUser(new UserCreateForm("admin", "jan", "kowalski",
                "admin", "1qaz2wsx", "jankowalski@email.com", 28.75));
        createUser(new UserCreateForm("employee", "julian", "nowak",
                "employee", "1qaz2wsx", "emp@email.com", 52.75));
    }
}
