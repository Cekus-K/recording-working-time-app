package pl.cekus.antologicproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);

    @Query("select u from users u where u.login like %:login% and " +
            "u.firstName like %:firstName% and u.lastName like %:lastName% and " +
            "u.costPerHour > :minCost and u.costPerHour < :maxCost and u.role = :role")
    List<User> findUsersWithFilters(@Param("login") String login, @Param("firstName") String firstName,
                                    @Param("lastName") String lastName, @Param("minCost") Double minCost,
                                    @Param("maxCost") Double maxCost,
                                    @Param("role") Role role);
}
