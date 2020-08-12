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
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (userFilterForm.getLogin() != null) {
            predicates.add(cb.like(root.get("login"), "%" + userFilterForm.getLogin() + "%"));
        }

        if (userFilterForm.getFirstName() != null) {
            predicates.add(cb.like(root.get("firstName"), "%" + userFilterForm.getFirstName() + "%"));
        }

        if (userFilterForm.getLastName() != null) {
            predicates.add(cb.like(root.get("lastName"), "%" + userFilterForm.getLastName() + "%"));
        }

        if (userFilterForm.getRole() != null) {
            try {
                Role userRole = Role.valueOf(userFilterForm.getRole().toUpperCase());
                predicates.add(cb.equal(root.get("role"), userRole));
            } catch (IllegalArgumentException e) {
                System.err.println("an invalid user role was provided");
            }
        }

        if (userFilterForm.getMinCost() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("costPerHour"), userFilterForm.getMinCost()));
        }

        if (userFilterForm.getMaxCost() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("costPerHour"), userFilterForm.getMaxCost()));
        }

        return cq.where(cb.and(predicates.toArray(new Predicate[0])))
                .distinct(true).orderBy(cb.desc(root.get("id"))).getRestriction();
    }
}
