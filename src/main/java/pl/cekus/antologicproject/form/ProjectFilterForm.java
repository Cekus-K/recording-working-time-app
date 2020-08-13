package pl.cekus.antologicproject.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.cekus.antologicproject.model.User;

import java.time.LocalDate;
import java.util.Set;

@Getter
@AllArgsConstructor
public class ProjectFilterForm {

    private String projectName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Set<User> users;

    private Boolean budgetExceeded;
}
