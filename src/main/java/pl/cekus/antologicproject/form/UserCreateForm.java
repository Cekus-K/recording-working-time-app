package pl.cekus.antologicproject.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class UserCreateForm extends UserBaseForm {

    @NotBlank
    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;

    @Email(message = "incorrect email format")
    private String email;

    @Positive(message = "cost per hour must be greater than zero")
    private Double costPerHour;

    public UserCreateForm(String login, String firstName, String lastName, String role, String password, String email, Double costPerHour) {
        super(login, firstName, lastName, role);
        this.password = password;
        this.email = email;
        this.costPerHour = costPerHour;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Double getCostPerHour() {
        return costPerHour;
    }
}
