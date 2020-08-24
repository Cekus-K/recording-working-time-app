package pl.cekus.antologicproject.dto;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProjectViewDto {

    @EqualsAndHashCode.Include
    private UUID uuid;

    private String projectName;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal budget;

    private BigDecimal budgetPercentageUse;

    private List<UserDto> users;

    public void setBudgetPercentageUse(BigDecimal budgetPercentageUse) {
        this.budgetPercentageUse = budgetPercentageUse.setScale(2, RoundingMode.CEILING);
    }
}
