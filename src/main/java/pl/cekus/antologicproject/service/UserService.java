package pl.cekus.antologicproject.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.form.UserFilterForm;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.repository.UserRepository;
import pl.cekus.antologicproject.specification.UserSpecification;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserCreateForm userCreateForm) {
        if (userRepository.findByLogin(userCreateForm.getLogin()) == null) {
            User toCreate = mapCreateFormToUser(userCreateForm);
            return mapUserToUserDto(userRepository.save(toCreate));
        }
        return mapUserToUserDto(userRepository.findByLogin(userCreateForm.getLogin()));
    }

    public Optional<User> readUserById(Long id) {
        return userRepository.findById(id);
    }

    public Page<UserDto> readUsers(UserFilterForm filterForm, Pageable pageable) {
        Specification<User> specs = Objects.requireNonNull(UserSpecification.loginLike(filterForm.getLogin())
                .and(UserSpecification.firstNameLike(filterForm.getFirstName())))
                .and(UserSpecification.lastNameLike(filterForm.getLastName()))
                .and(UserSpecification.roleEqual(filterForm.getRole()))
                .and(UserSpecification.minimumCost(filterForm.getMinCost()))
                .and(UserSpecification.maximumCost(filterForm.getMaxCost()));
        return userRepository.findAll(specs, pageable)
                .map(this::mapUserToUserDto);
    }

    public void updateUser(Long id, UserCreateForm userCreateForm) {
        Optional<User> toUpdate = userRepository.findById(id);
        toUpdate.ifPresent(user -> setValuesToUpdatingUser(user, userCreateForm));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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

    private User mapCreateFormToUser(UserCreateForm createForm) {
        return new User(createForm.getLogin(), createForm.getFirstName(), createForm.getLastName(),
                mapStringToRole(createForm.getRole()), createForm.getPassword(), createForm.getEmail(), createForm.getCostPerHour());
    }

    private UserDto mapUserToUserDto(User user) {
        return new UserDto(user.getLogin(), user.getFirstName(), user.getLastName(),
                user.getRole(), user.getEmail(), user.getCostPerHour());
    }

    private Role mapStringToRole(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createTestUser() {
        createUser(new UserCreateForm("admin", "jan", "kowalski",
                "admin", "1qaz2wsx", "jankowalski@email.com", 28.75));
    }
}
