package pl.cekus.antologicproject.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.dto.UserReportDto;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.form.UserFilterForm;
import pl.cekus.antologicproject.service.UserService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api")
class UserController extends ResponseEntityExceptionHandler {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    UserDto createUser(@RequestBody @Valid UserCreateForm user) {
        return userService.createUser(user);
    }

    @GetMapping("/users")
    Page<UserDto> readUsers(UserFilterForm userFilterForm, Pageable pageable) {
        return userService.readUsersWithFilters(userFilterForm, pageable);
    }

    @PutMapping("/users/{uuid}")
    void updateUser(@PathVariable UUID uuid, @RequestBody @Valid UserCreateForm user) {
        userService.updateUser(uuid, user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{uuid}")
    void deleteUser(@PathVariable UUID uuid) {
        userService.deleteUser(uuid);
    }

    @GetMapping("/users/report")
    UserReportDto getUserReport(@RequestParam(name = "employee") String login,
                                @RequestParam(name = "time-period", defaultValue = "all") String timePeriod) {
        return userService.getUserReport(login, timePeriod);
    }
}
