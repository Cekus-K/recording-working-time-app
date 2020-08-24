package pl.cekus.antologicproject.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.cekus.antologicproject.form.ProjectFilterForm;
import pl.cekus.antologicproject.model.ProjectView;
import pl.cekus.antologicproject.model.ProjectView_;
import pl.cekus.antologicproject.model.User_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ProjectViewSpecification implements Specification<ProjectView> {

    private ProjectFilterForm projectFilterForm;

    public ProjectViewSpecification(ProjectFilterForm projectFilterForm) {
        this.projectFilterForm = projectFilterForm;
    }

    @Override
    public Predicate toPredicate(Root<ProjectView> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (projectFilterForm.getProjectName() != null) {
            predicates.add(criteriaBuilder.like(root.get(ProjectView_.projectName), "%" + projectFilterForm.getProjectName() + "%"));
        }

        if (projectFilterForm.getStartDate() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ProjectView_.startDate), projectFilterForm.getStartDate()));
        }

        if (projectFilterForm.getEndDate() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(ProjectView_.endDate), projectFilterForm.getEndDate()));
        }

        if (projectFilterForm.getUsers() != null) {
            Predicate[] predicatesArray = new Predicate[projectFilterForm.getUsers().size() + 1];
            for (int i = 0; i < projectFilterForm.getUsers().size(); i++) {
                predicatesArray[i] = criteriaBuilder.equal(root.join(ProjectView_.users)
                        .get(User_.login), projectFilterForm.getUsers().get(i));
            }
            predicatesArray[predicatesArray.length - 1] = criteriaBuilder.like(root.join(ProjectView_.users)
                    .get(User_.login), "%%");

            predicates.add(criteriaBuilder.and(predicatesArray));
        }

        if (projectFilterForm.getBudgetExceeded() != null) {
            predicates.add(criteriaBuilder.equal(root.get(ProjectView_.budgetExceeded), projectFilterForm.getBudgetExceeded()));
        }

        return criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .distinct(true).orderBy(criteriaBuilder.desc(root.get(ProjectView_.uuid))).getRestriction();
    }
}
