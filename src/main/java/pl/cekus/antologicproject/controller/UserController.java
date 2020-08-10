package pl.cekus.antologicproject.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.service.UserService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
class UserController extends ResponseEntityExceptionHandler {

    private UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        User result = userService.createUser(user);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @GetMapping("/user")
    ResponseEntity<List<User>> readUsers() {
        return ResponseEntity.ok().body(userService.readUsers());
    }

    @PutMapping("/user/{id}")
    ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody @Valid User user) {
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
