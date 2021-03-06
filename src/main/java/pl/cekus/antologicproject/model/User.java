package pl.cekus.antologicproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends AbstractEntity {

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @Column(name = "cost_per_hour", nullable = false)
    private BigDecimal costPerHour;

    @ManyToMany(targetEntity = Project.class, fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Project> projects = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkingTime> workingTimes = new HashSet<>();

    public User(String login, String firstName, String lastName, Role role, String password, String email, BigDecimal costPerHour) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.password = password;
        this.email = email;
        this.costPerHour = costPerHour;
    }

    @JsonIgnore
    public Set<Project> getProjects() {
        return projects;
    }

    public void addWorkingTime(WorkingTime workingTime) {
        this.workingTimes.add(workingTime);
        workingTime.setUser(this);
        workingTime.setProject(workingTime.getProject());
    }

    public void removeWorkingTime(WorkingTime workingTime) {
        this.workingTimes.remove(workingTime);
        workingTime.setUser(null);
        workingTime.setProject(null);
    }
}
