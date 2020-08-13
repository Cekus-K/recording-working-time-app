package pl.cekus.antologicproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.cekus.antologicproject.model.WorkingTime;

@Repository
public interface WorkingTimeRepository extends JpaRepository<WorkingTime, Long> {
}
