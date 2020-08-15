package pl.cekus.antologicproject.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.cekus.antologicproject.form.ProjectFilterForm;
import pl.cekus.antologicproject.model.Project;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification implements Specification<Project> {

    private ProjectFilterForm projectFilterForm;

    public ProjectSpecification(ProjectFilterForm projectFilterForm) {
        this.projectFilterForm = projectFilterForm;
    }

    @Override
    public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        root.fetch(Project_.USERS, JoinType.LEFT);

        if (projectFilterForm.getProjectName() != null) {
            predicates.add(criteriaBuilder.like(root.get(Project_.PROJECT_NAME), "%" + projectFilterForm.getProjectName() + "%"));
        }

        if (projectFilterForm.getStartDate() != null && projectFilterForm.getEndDate() != null) {
            predicates.add(
                    criteriaBuilder.and(
                            criteriaBuilder.between(root.get(Project_.START_DATE), projectFilterForm.getStartDate(), projectFilterForm.getEndDate()),
                            criteriaBuilder.between(root.get(Project_.END_DATE), projectFilterForm.getStartDate(), projectFilterForm.getEndDate())
                    )
            );
        }

        if (projectFilterForm.getUsers() != null) {
            projectFilterForm.getUsers().forEach(user ->
                    predicates.add(criteriaBuilder.equal(root.join(Project_.USERS).get(User_.LOGIN), user.getLogin()))
            );
        }

        // TODO:
        if (projectFilterForm.getBudgetExceeded() != null) {
        }

        return criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .distinct(true).orderBy(criteriaBuilder.desc(root.get(Project_.ID))).getRestriction();
    }
}
