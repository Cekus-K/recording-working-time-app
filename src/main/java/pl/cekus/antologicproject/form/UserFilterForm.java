package pl.cekus.antologicproject.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.cekus.antologicproject.model.Role;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class UserFilterForm {

    private String login;

    private String firstName;

    private String lastName;

    private Role role;

    private BigDecimal minCost;

    private BigDecimal maxCost;
}
