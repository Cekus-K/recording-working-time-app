package pl.cekus.antologicproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserReportDto {
    private BigDecimal totalCost;
    private List<UserSingleProjectReport> singleProjectReports;

    @Getter
    @AllArgsConstructor
    public static class UserSingleProjectReport {
        private BigDecimal projectCost;
        private Double projectSpentTime;
        private String projectName;
    }
}
