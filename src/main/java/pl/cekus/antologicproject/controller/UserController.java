package pl.cekus.antologicproject.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.cekus.antologicproject.dto.UserDto;
import pl.cekus.antologicproject.form.UserCreateForm;
import pl.cekus.antologicproject.form.UserFilterForm;
import pl.cekus.antologicproject.service.UserService;

import javax.validation.Valid;

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

    @PutMapping("/users/{id}")
    ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody @Valid UserCreateForm user) {
        if (userService.readUserById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.updateUser(id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/users/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.readUserById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
