package pl.cekus.antologicproject.model;

import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "projects_view")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProjectView {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    @EqualsAndHashCode.Include
    private UUID uuid;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "budget")
    private BigDecimal budget;

    @ManyToMany(targetEntity = User.class, mappedBy = "projects", fetch = FetchType.LAZY)
    private Set<User> users;

    @Column(name = "budget_percentage_use")
    private Double budgetPercentageUse;

    @Column(name = "budget_exceeded")
    private Boolean budgetExceeded;
}
