package pl.cekus.antologicproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.dto.UserReportDto;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.form.UserFilterForm;
import pl.cekus.antologicproject.mapper.UserMapper;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.repository.UserRepository;
import pl.cekus.antologicproject.specification.UserSpecification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.cekus.antologicproject.utills.ReportUtil.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto createUser(UserCreateForm userCreateForm) {
        if (userRepository.findByLogin(userCreateForm.getLogin()).isPresent()) {
            throw new IllegalArgumentException("provided login already exists");
        }
        User toCreate = userMapper.mapUserCreateFormToUser(userCreateForm);
        return userMapper.mapUserToUserDto(userRepository.save(toCreate));
    }

    public Optional<User> readUserById(Long id) {
        return userRepository.findById(id);
    }

    public Page<UserDto> readUsersWithFilters(UserFilterForm filterForm, Pageable pageable) {
        UserSpecification specification = new UserSpecification(filterForm);
        return userRepository.findAll(specification, pageable)
                .map(userMapper::mapUserToUserDto);
    }

    public void updateUser(Long id, UserCreateForm userCreateForm) {
        Optional<User> toUpdate = userRepository.findById(id);
        toUpdate.ifPresent(user -> {
            if (!user.getLogin().equals(userCreateForm.getLogin())) {
                checkIfLoginAlreadyExists(userCreateForm.getLogin());
            }
            setValuesToUpdatingUser(user, userCreateForm);
            userRepository.save(user);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserReportDto getUserReport(String login, String timePeriod) {
        TimePeriod period = TimePeriod.valueOf(timePeriod.toUpperCase());
        User user = findUserByLogin(login);

        BigDecimal totalCost = user.getProjects().stream()
                .map(project -> calculateUserCostInProject(project, user, period))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<UserReportDto.SingleProjectFromUserReport> singleReports = user.getProjects().stream()
                .map(project -> {
                    Double timeInProject = calculateUserTimeInProject(project, user, period);
                    BigDecimal costInProject = user.getCostPerHour().multiply(BigDecimal.valueOf(timeInProject));
                    return new UserReportDto.SingleProjectFromUserReport(project.getProjectName(), timeInProject,
                            costInProject.setScale(2, RoundingMode.DOWN));
                })
                .collect(Collectors.toList());
        return new UserReportDto(totalCost.setScale(2, RoundingMode.DOWN), singleReports);
    }

    User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("provided user not found"));
    }

    // FIXME: write own exceptions
    private void checkIfLoginAlreadyExists(String login) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalStateException("provided login already exists");
        }
    }

    private void setValuesToUpdatingUser(User toUpdate, UserCreateForm createForm) {
        toUpdate.setLogin(createForm.getLogin());
        toUpdate.setFirstName(createForm.getFirstName());
        toUpdate.setLastName(createForm.getLastName());
        toUpdate.setRole(createForm.getRole());
        toUpdate.setPassword(createForm.getPassword());
        toUpdate.setEmail(createForm.getEmail());
        toUpdate.setCostPerHour(createForm.getCostPerHour());
    }
}
