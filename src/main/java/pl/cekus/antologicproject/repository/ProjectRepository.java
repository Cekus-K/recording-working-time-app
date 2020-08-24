package pl.cekus.antologicproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cekus.antologicproject.model.Project;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @Query("select p from Project p " +
            "left join fetch p.users u " +
            "left join fetch u.workingTimes wt " +
            "where p.projectName = :projectName")
    Optional<Project> findByProjectName(@Param("projectName") String projectName);

    Optional<Project> findByUuid(UUID uuid);

    boolean existsByProjectName(String projectName);

    void deleteByUuid(UUID uuid);
}
