package pl.cekus.antologicproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.dto.UserReportDto;
import pl.cekus.antologicproject.exception.NotFoundException;
import pl.cekus.antologicproject.exception.NotUniqueException;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.form.UserFilterForm;
import pl.cekus.antologicproject.mapper.UserMapper;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.repository.UserRepository;
import pl.cekus.antologicproject.specification.UserSpecification;
import pl.cekus.antologicproject.timeperiod.TimePeriod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.cekus.antologicproject.utills.ReportUtil.calculateUserCostInProject;
import static pl.cekus.antologicproject.utills.ReportUtil.calculateUserTimeInProject;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto createUser(UserCreateForm userCreateForm) {
        if (userRepository.existsByLogin(userCreateForm.getLogin())) {
            throw new NotUniqueException("provided login already exists");
        }
        User toCreate = userMapper.mapUserCreateFormToUser(userCreateForm);
        return userMapper.mapUserToUserDto(userRepository.save(toCreate));
    }

    public Page<UserDto> readUsersWithFilters(UserFilterForm filterForm, Pageable pageable) {
        UserSpecification specification = new UserSpecification(filterForm);
        return userRepository.findAll(specification, pageable)
                .map(userMapper::mapUserToUserDto);
    }

    public void updateUser(UUID uuid, UserCreateForm userCreateForm) {
        User toUpdate = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("provided user uuid not found"));
        if (!toUpdate.getLogin().equals(userCreateForm.getLogin())) {
            checkIfLoginAlreadyExists(userCreateForm.getLogin());
        }
        userMapper.fromUserCreateFormToUser(userCreateForm, toUpdate);
        userRepository.save(toUpdate);
    }

    public void deleteUser(UUID uuid) {
        if (!userRepository.existsByUuid(uuid)) {
            throw new NotFoundException("provided user uuid not found");
        }
        userRepository.deleteByUuid(uuid);
    }

    public UserReportDto getUserReport(String login, TimePeriod timePeriod) {
        User user = findUserByLogin(login);
        BigDecimal totalCost = calculateTotalCostInProject(timePeriod, user);
        List<UserReportDto.SingleProjectFromUserReport> singleReports = getListOfSingleProjectReports(timePeriod, user);
        return new UserReportDto(totalCost.setScale(2, RoundingMode.CEILING), singleReports);
    }

    User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("provided user not found"));
    }

    private BigDecimal calculateTotalCostInProject(TimePeriod timePeriod, User user) {
        return user.getProjects().stream()
                .map(project -> calculateUserCostInProject(project, user, timePeriod))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<UserReportDto.SingleProjectFromUserReport> getListOfSingleProjectReports(TimePeriod timePeriod, User user) {
        return user.getProjects().stream()
                .map(project -> {
                    double timeInProject = calculateUserTimeInProject(project, user, timePeriod);
                    BigDecimal costInProject = user.getCostPerHour().multiply(BigDecimal.valueOf(timeInProject));
                    return new UserReportDto.SingleProjectFromUserReport(project.getProjectName(), timeInProject,
                            costInProject.setScale(2, RoundingMode.CEILING));
                })
                .collect(Collectors.toList());
    }

    private void checkIfLoginAlreadyExists(String login) {
        if (userRepository.existsByLogin(login)) {
            throw new NotUniqueException("provided login already exists");
        }
    }
}
