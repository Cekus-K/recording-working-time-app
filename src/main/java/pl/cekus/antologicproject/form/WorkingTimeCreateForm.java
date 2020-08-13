package pl.cekus.antologicproject.form;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WorkingTimeCreateForm {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull
    private String userLogin;

    @NotNull
    private String projectName;
}
