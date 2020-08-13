package pl.cekus.antologicproject.form;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.*;

@Getter
@AllArgsConstructor
public class UserCreateForm {

    @NotBlank
    private String login;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private String role;

    @NotBlank
    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;

    @Email(message = "incorrect email format")
    private String email;

    @Positive(message = "cost per hour must be greater than zero")
    private Double costPerHour;
}
