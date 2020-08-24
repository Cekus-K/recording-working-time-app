package pl.cekus.antologicproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cekus.antologicproject.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("select u from User u " +
            "left join fetch u.projects p " +
            "left join fetch p.workingTimes wt " +
            "where u.login = :login")
    Optional<User> findByLogin(@Param("login") String login);

    Optional<User> findByUuid(UUID uuid);

    boolean existsByLogin(String login);

    boolean existsByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
