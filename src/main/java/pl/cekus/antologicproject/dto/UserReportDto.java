package pl.cekus.antologicproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserReportDto {
    private BigDecimal totalUserCost;
    private List<SingleProjectFromUserReport> projectsFromUser;

    @Getter
    @AllArgsConstructor
    public static class SingleProjectFromUserReport {
        private String projectName;
        private Double timeInProject;
        private BigDecimal costInProject;
    }
}
