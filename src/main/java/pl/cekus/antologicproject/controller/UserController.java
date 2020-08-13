package pl.cekus.antologicproject.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.dto.WorkingTimeDto;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.form.UserFilterForm;
import pl.cekus.antologicproject.form.WorkingTimeCreateForm;
import pl.cekus.antologicproject.service.UserService;
import pl.cekus.antologicproject.service.WorkingTimeService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
class UserController extends ResponseEntityExceptionHandler {

    private final UserService userService;
    private final WorkingTimeService workingTimeService;

    UserController(UserService userService, WorkingTimeService workingTimeService) {
        this.userService = userService;
        this.workingTimeService = workingTimeService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user")
    UserDto createUser(@RequestBody @Valid UserCreateForm user) {
        return userService.createUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users")
    Page<UserDto> readUsers(UserFilterForm userFilterForm, Pageable pageable) {
        return userService.readUsersWithFilters(userFilterForm, pageable);
    }

    @PutMapping("/user/{id}")
    ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody @Valid UserCreateForm user) {
        if (userService.readUserById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.updateUser(id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.readUserById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user/working-time/add")
    void addWorkingTime(@RequestBody WorkingTimeCreateForm workingTimeCreateForm) {
        workingTimeService.addWorkingTimeToUser(workingTimeCreateForm);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/working-time")
    List<WorkingTimeDto> addWorkingTime(@RequestParam(name = "employee") String employee) {
        return workingTimeService.readAllWorkingTimes(employee);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/user/working-time/{id}")
    void updateWorkingTime(@PathVariable Long id, @RequestBody @Valid WorkingTimeCreateForm workingTime) {
        workingTimeService.updateWorkingTime(id, workingTime);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/user/working-time/{id}")
    void deleteWorkingTime(@PathVariable Long id) {
        workingTimeService.deleteWorkingTime(id);
    }
}
