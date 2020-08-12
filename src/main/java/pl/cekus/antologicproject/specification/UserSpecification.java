package pl.cekus.antologicproject.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;

public class UserSpecification {

    public static Specification<User> loginLike(String login) {
        if (login == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("login"), "%" + login + "%");
    }

    public static Specification<User> firstNameLike(String firstName) {
        if (firstName == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%");
    }

    public static Specification<User> lastNameLike(String lastName) {
        if (lastName == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%");
    }

    public static Specification<User> roleEqual(String role) {
        if (role == null) {
            return null;
        }
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role"), userRole);
        } catch (IllegalArgumentException e) {
            System.err.println("an invalid user role was provided");
            return null;
        }
    }

    public static Specification<User> passwordLike(String password) {
        if (password == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("password"), "%" + password + "%");
    }

    public static Specification<User> emailLike(String email) {
        if (email == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("email"), "%" + email + "%");
    }

    public static Specification<User> minimumCost(Double minCost) {
        if (minCost == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("costPerHour"), minCost);
    }

    public static Specification<User> maximumCost(Double maxCost) {
        if (maxCost == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("costPerHour"), maxCost);
    }
}
