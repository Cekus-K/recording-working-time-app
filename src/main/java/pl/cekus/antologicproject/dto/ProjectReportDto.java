package pl.cekus.antologicproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectReportDto {
    private BigDecimal totalProjectCost;
    private Double totalProjectTime;
    private Boolean budgetExceeded;
    private List<SingleUserInProjectReport> usersInProject;

    @Getter
    @AllArgsConstructor
    public static class SingleUserInProjectReport {
        private String userLogin;
        private Double timeInProject;
        private BigDecimal costInProject;
    }
}
