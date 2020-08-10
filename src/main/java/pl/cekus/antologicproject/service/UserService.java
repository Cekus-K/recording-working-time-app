package pl.cekus.antologicproject.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (userRepository.findByLogin(user.getLogin()) == null) {
            return userRepository.save(user);
        }
        return userRepository.findByLogin(user.getLogin());
    }

    public List<User> readUsers() {
        return userRepository.findAll();
    }

    public Optional<User> readUserById(Long id) {
        return userRepository.findById(id);
    }

    public void updateUser(Long id, User user) {
        Optional<User> toUpdate = userRepository.findById(id);

        toUpdate.ifPresent(updatedUser -> {
            updatedUser.setLogin(user.getLogin());
            updatedUser.setFirstName(user.getFirstName());
            updatedUser.setLastName(user.getLastName());
            updatedUser.setRole(user.getRole());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setCostPerHour(user.getCostPerHour());
            userRepository.save(updatedUser);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createTestUser() {
        createUser(new User("admin", "jan", "kowalski",
                "admin", "1qaz2wsx", "jankowalski@email.com", 28.75));
    }
}
