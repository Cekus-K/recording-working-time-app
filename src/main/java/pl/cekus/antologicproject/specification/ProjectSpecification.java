package pl.cekus.antologicproject.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.cekus.antologicproject.form.ProjectFilterForm;
import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.Project_;
import pl.cekus.antologicproject.model.User_;

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
        root.fetch(Project_.users, JoinType.LEFT);

        if (projectFilterForm.getProjectName() != null) {
            predicates.add(criteriaBuilder.like(root.get(Project_.projectName), "%" + projectFilterForm.getProjectName() + "%"));
        }

        if (projectFilterForm.getStartDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Project_.startDate), projectFilterForm.getStartDate()));
        }

        if (projectFilterForm.getEndDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Project_.endDate), projectFilterForm.getEndDate()));
        }

        if (projectFilterForm.getUsers() != null) {
            projectFilterForm.getUsers().forEach(user ->
                    predicates.add(criteriaBuilder.equal(root.join(Project_.users).get(User_.login), user.getLogin()))
            );
        }

        // TODO:
        if (projectFilterForm.getBudgetExceeded() != null) {
        }

        return criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .distinct(true).orderBy(criteriaBuilder.desc(root.get(Project_.id))).getRestriction();
    }
}
