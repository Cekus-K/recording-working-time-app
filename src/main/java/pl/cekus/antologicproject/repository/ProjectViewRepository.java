package pl.cekus.antologicproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.cekus.antologicproject.model.ProjectView;

public interface ProjectViewRepository extends JpaRepository<ProjectView, String>,
        JpaSpecificationExecutor<ProjectView> {

    @Override
    @EntityGraph(attributePaths = {"users"})
    Page<ProjectView> findAll(Specification<ProjectView> spec, Pageable pageable);
}
