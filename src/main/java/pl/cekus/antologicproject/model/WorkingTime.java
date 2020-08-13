package pl.cekus.antologicproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "working_times")
@Getter
@Setter
@NoArgsConstructor
public class WorkingTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public WorkingTime(LocalDateTime startTime, LocalDateTime endTime, User user, Project project) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.project = project;
    }
}
