package pl.cekus.antologicproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@AllArgsConstructor
public class ProjectDto {

    private String projectName;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double budget;

    private Set<UserDto> users;
}
