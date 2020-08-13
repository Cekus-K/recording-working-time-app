package pl.cekus.antologicproject.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.cekus.antologicproject.form.UserFilterForm;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification implements Specification<User> {

    private UserFilterForm userFilterForm;

    public UserSpecification(UserFilterForm userFilterForm) {
        this.userFilterForm = userFilterForm;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (userFilterForm.getLogin() != null) {
            predicates.add(criteriaBuilder.like(root.get(User_.LOGIN), "%" + userFilterForm.getLogin() + "%"));
        }

        if (userFilterForm.getFirstName() != null) {
            predicates.add(criteriaBuilder.like(root.get(User_.FIRST_NAME), "%" + userFilterForm.getFirstName() + "%"));
        }

        if (userFilterForm.getLastName() != null) {
            predicates.add(criteriaBuilder.like(root.get(User_.LAST_NAME), "%" + userFilterForm.getLastName() + "%"));
        }

        if (userFilterForm.getRole() != null) {
            try {
                Role userRole = Role.valueOf(userFilterForm.getRole().toUpperCase());
                predicates.add(criteriaBuilder.equal(root.get(User_.ROLE), userRole));
            } catch (IllegalArgumentException e) {
                System.err.println("an invalid user role was provided");
            }
        }

        if (userFilterForm.getMinCost() != null) {
            predicates.add(criteriaBuilder.between(root.get(User_.COST_PER_HOUR), userFilterForm.getMinCost(),
                    userFilterForm.getMaxCost()));
        }

        return criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .distinct(true).orderBy(criteriaBuilder.desc(root.get(User_.ID))).getRestriction();
    }
}
