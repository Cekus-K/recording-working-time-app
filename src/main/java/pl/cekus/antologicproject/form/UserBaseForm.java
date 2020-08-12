package pl.cekus.antologicproject.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserBaseForm {

    @NotBlank
    private String login;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private String role;

    UserBaseForm(String login, String firstName, String lastName, String role) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
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

    public String getRole() {
        return role;
    }

}
