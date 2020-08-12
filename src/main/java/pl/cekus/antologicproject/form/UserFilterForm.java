package pl.cekus.antologicproject.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class UserFilterForm extends UserBaseForm {

    public static final Double MIN_COST_PER_HOUR = 0.01;
    public static final Double MAX_COST_PER_HOUR = 1000.00;

    @NotBlank
    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;

    @Email(message = "incorrect email format")
    private String email;

    @Positive(message = "cost per hour must be greater than zero")
    private Double minCost;

    @Positive(message = "cost per hour must be greater than zero")
    private Double maxCost;

    public UserFilterForm(String login, String firstName, String lastName, String role,
                          String password, String email, Double minCost, Double maxCost) {
        super(login, firstName, lastName, role);
        this.password = password;
        this.email = email;
        this.minCost = minCost;
        this.maxCost = maxCost;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Double getMinCost() {
        return minCost;
    }

    public Double getMaxCost() {
        return maxCost;
    }
}
