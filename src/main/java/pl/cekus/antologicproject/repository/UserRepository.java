package pl.cekus.antologicproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.cekus.antologicproject.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByLogin(String login);

//    @Query("select u from User u where u.login like %:login% and " +
//            "u.firstName like %:firstName% and u.lastName like %:lastName% and " +
//            "u.costPerHour > :minCost and u.costPerHour < :maxCost and u.role = :role")
//    Page<User> findUsersWithFilters(@Param("login") String login, @Param("firstName") String firstName,
//                                    @Param("lastName") String lastName, @Param("minCost") Double minCost,
//                                    @Param("maxCost") Double maxCost, @Param("role") Role role, Pageable pageable);
}
