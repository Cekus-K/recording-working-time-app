package pl.cekus.antologicproject.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.cekus.antologicproject.model.Role;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class UserCreateForm {

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
    private BigDecimal costPerHour;
}
