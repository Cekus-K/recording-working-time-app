package pl.cekus.antologicproject.service;

import org.junit.jupiter.api.Test;
import pl.cekus.antologicproject.mapper.UserMapper;
import pl.cekus.antologicproject.mapper.UserMapperImpl;
import pl.cekus.antologicproject.repository.UserRepository;
import pl.cekus.antologicproject.repository.UserRepositoryStub;

class UserServiceTest {

    private UserRepository userRepository = new UserRepositoryStub();
    private UserMapper userMapper = new UserMapperImpl();
    private UserService userService = new UserService(userRepository, userMapper);

    @Test
    void createUser() {
    }

    @Test
    void readUsersWithFilters() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getUserReport() {
    }

    @Test
    void findUserByLogin() {
    }
}
