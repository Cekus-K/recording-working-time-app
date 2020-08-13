package pl.cekus.antologicproject.form;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserFilterForm {

    private String login;

    private String firstName;

    private String lastName;

    private String role;

    private Double minCost;

    private Double maxCost;
}
