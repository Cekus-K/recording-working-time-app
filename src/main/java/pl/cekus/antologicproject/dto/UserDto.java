package pl.cekus.antologicproject.dto;

import pl.cekus.antologicproject.model.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class UserDto {

    @NotBlank
    private String login;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private Role role;

    @Email(message = "incorrect email format")
    private String email;

    @Positive(message = "cost per hour must be greater than zero")
    private Double costPerHour;

    public UserDto(String login, String firstName, String lastName, Role role, String email, Double costPerHour) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.costPerHour = costPerHour;
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

    public Role getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public Double getCostPerHour() {
        return costPerHour;
    }
}
