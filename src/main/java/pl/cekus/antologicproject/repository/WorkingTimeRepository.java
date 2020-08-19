package pl.cekus.antologicproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.cekus.antologicproject.model.WorkingTime;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkingTimeRepository extends JpaRepository<WorkingTime, Long> {

    Optional<WorkingTime> findByUuid(UUID uuid);
}
