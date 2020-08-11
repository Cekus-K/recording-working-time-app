package pl.cekus.antologicproject.dto;

import pl.cekus.antologicproject.model.Role;

public class UserFiltersForm {

    public static final Double MIN_COST_PER_HOUR = 0.01;
    public static final Double MAX_COST_PER_HOUR = 1000.00;

    private String login;
    private String firstName;
    private String lastName;
    private Double minCost;
    private Double maxCost;
    private Role role;

    UserFiltersForm(String login, String firstName, String lastName, String role, Double minCost, Double maxCost) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        try {
            this.minCost = minCost;
            this.maxCost = maxCost;
        } catch (NumberFormatException e) {
            this.minCost = MIN_COST_PER_HOUR;
            this.maxCost = MAX_COST_PER_HOUR;
        }
        this.role = Role.valueOf(role.toUpperCase());
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Double getMinCost() {
        return minCost;
    }

    public Double getMaxCost() {
        return maxCost;
    }

    public Role getRole() {
        return role;
    }
}
