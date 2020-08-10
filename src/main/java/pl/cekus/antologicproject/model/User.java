package pl.cekus.antologicproject.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String login;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    private Role role;
    @NotBlank
    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;
    @Email(message = "incorrect email format")
    private String email;
    @Positive(message = "cost per hour must be greater than zero")
    private Double costPerHour;

    public User() {
    }

    public User(String login, String firstName, String lastName, String role, String password, String email, Double costPerHour) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = Role.valueOf(role.toUpperCase());
        this.password = password;
        this.email = email;
        this.costPerHour = costPerHour;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role.toString();
    }

    public void setRole(String role) {
        this.role = Role.valueOf(role.toUpperCase());
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getCostPerHour() {
        return costPerHour;
    }

    public void setCostPerHour(Double costPerHour) {
        this.costPerHour = costPerHour;
    }
}
