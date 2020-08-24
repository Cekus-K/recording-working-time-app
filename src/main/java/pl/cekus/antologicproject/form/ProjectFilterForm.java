package pl.cekus.antologicproject.form;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectFilterForm {

    private String projectName;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<String> users;

    private Boolean budgetExceeded;
}
