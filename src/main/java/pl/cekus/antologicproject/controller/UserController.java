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
import java.net.URI;

@RestController
@RequestMapping("/api")
class UserController extends ResponseEntityExceptionHandler {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateForm user) {
        UserDto result = userService.createUser(user);
        return ResponseEntity.created(URI.create("/" + result.getLogin())).body(result);
    }

    @GetMapping("/users")
    ResponseEntity<Page<UserDto>> readUsers(@RequestParam(name = "login", defaultValue = "") String login,
                                            @RequestParam(name = "firstName", required = false) String firstName,
                                            @RequestParam(name = "lastName", required = false) String lastName,
                                            @RequestParam(name = "role", required = false) String role,
                                            @RequestParam(name = "minCost", required = false) double minCost,
                                            @RequestParam(name = "maxCost", required = false) double maxCost,
                                            Pageable pageable) {
        return ResponseEntity.ok().body(userService.readUsers(new UserFilterForm(login, firstName,
                lastName, role, minCost, maxCost), pageable));
    }

    @PutMapping("/user/{id}")
    ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody @Valid UserCreateForm user) {
        if (userService.readUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.updateUser(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/user/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.readUserById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
