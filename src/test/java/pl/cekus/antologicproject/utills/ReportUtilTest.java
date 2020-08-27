package pl.cekus.antologicproject.utills;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.model.WorkingTime;
import pl.cekus.antologicproject.timeperiod.TimePeriod;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReportUtilTest {

    // the time difference is 1 hour and 15 minutes
    private LocalDateTime workingTimeStart = LocalDateTime.of(2020, 8, 15, 15, 0, 0);
    private LocalDateTime workingTimeEnd = LocalDateTime.of(2020, 8, 15, 16, 15, 0);


    private Project project1;
    private Project project2;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        project1 = new Project("project1", "test desc", LocalDate.now(), LocalDate.now().plusDays(90), BigDecimal.valueOf(2000));
        project2 = new Project("project2", "test desc", LocalDate.now(), LocalDate.now().plusDays(90), BigDecimal.valueOf(3000));
        user1 = new User("user1", "firstName1", "lastName1", Role.EMPLOYEE, "123456", "test1@email.com", BigDecimal.valueOf(10.0));
        user2 = new User("user2", "firstName2", "lastName2", Role.EMPLOYEE, "123456", "test2@email.com", BigDecimal.valueOf(12.50));
        user3 = new User("user3", "firstName3", "lastName3", Role.EMPLOYEE, "123456", "test3@email.com", BigDecimal.valueOf(15.75));
    }

    @AfterEach
    void tearDown() {
        project1 = null;
        project2 = null;
        user1 = null;
        user2 = null;
        user3 = null;
    }

    @Test
    void shouldCalculateTotalCostInProjectWhenUserIsInProjectAndWorkingTimesAreAddedToProjectAndUser() {
        //given
        project1.addUser(user1);
        WorkingTime workingTime1 = new WorkingTime(workingTimeStart, workingTimeEnd, user1, project1);
        WorkingTime workingTime2 = new WorkingTime(workingTimeStart.minusHours(1), workingTimeEnd, user1, project1);
        user1.addWorkingTime(workingTime1);
        user1.addWorkingTime(workingTime2);
        project1.getWorkingTimes().add(workingTime1);
        project1.getWorkingTimes().add(workingTime2);

        Double time1 = Duration.between(workingTime1.getStartTime(), workingTime1.getEndTime()).toSeconds() / 3600.0;
        Double time2 = Duration.between(workingTime2.getStartTime(), workingTime2.getEndTime()).toSeconds() / 3600.0;
        BigDecimal costPerHour = user1.getCostPerHour();

        //when
        BigDecimal totalCost = ReportUtil.calculateUserCostInProject(project1, user1, null);

        //then
        assertThat(totalCost).isEqualTo(costPerHour.multiply(BigDecimal.valueOf(time1 + time2)));
    }

    @Test
    void shouldCalculateTotalCostInProjectFromWeekWhenUserIsInProjectAndWorkingTimesAreAddedToProjectAndUser() {
        //given
        project1.addUser(user1);
        WorkingTime workingTime1 = new WorkingTime(workingTimeStart, workingTimeEnd, user1, project1);
        WorkingTime workingTime2 = new WorkingTime(workingTimeStart.minusHours(1), workingTimeEnd, user1, project1);
        user1.addWorkingTime(workingTime1);
        user1.addWorkingTime(workingTime2);
        project1.getWorkingTimes().add(workingTime1);
        project1.getWorkingTimes().add(workingTime2);

        BigDecimal costPerHour = user1.getCostPerHour();

        //when
        BigDecimal totalCost = ReportUtil.calculateUserCostInProject(project1, user1, TimePeriod.CURRENT_WEEK);

        //then
        assertThat(totalCost).isEqualTo(costPerHour.multiply(BigDecimal.valueOf(0.0)));
    }

    @Test
    void shouldCalculateTotalTimeInProjectWhenUserIsInProjectAndWorkingTimesAreAddedToProjectAndUser() {
        //given
        project1.addUser(user1);
        WorkingTime workingTime1 = new WorkingTime(workingTimeStart, workingTimeEnd, user1, project1);
        WorkingTime workingTime2 = new WorkingTime(workingTimeStart.minusHours(1), workingTimeEnd, user1, project1);
        user1.addWorkingTime(workingTime1);
        user1.addWorkingTime(workingTime2);
        project1.getWorkingTimes().add(workingTime1);
        project1.getWorkingTimes().add(workingTime2);

        Double time1 = Duration.between(workingTime1.getStartTime(), workingTime1.getEndTime()).toSeconds() / 3600.0;
        Double time2 = Duration.between(workingTime2.getStartTime(), workingTime2.getEndTime()).toSeconds() / 3600.0;

        //when
        Double totalTime = ReportUtil.calculateUserTimeInProject(project1, user1, TimePeriod.CURRENT_YEAR);

        //then
        assertThat(totalTime).isEqualTo(time1 + time2);
    }

    @Test
    void shouldCalculateTotalTimeInProjectFromCurrentWeekWhenUserIsInProjectAndWorkingTimesAreAddedToProjectAndUser() {
        //given
        project1.addUser(user1);
        WorkingTime workingTime1 = new WorkingTime(workingTimeStart, workingTimeEnd, user1, project1);
        WorkingTime workingTime2 = new WorkingTime(workingTimeStart.minusHours(1), workingTimeEnd, user1, project1);
        user1.addWorkingTime(workingTime1);
        user1.addWorkingTime(workingTime2);
        project1.getWorkingTimes().add(workingTime1);
        project1.getWorkingTimes().add(workingTime2);

        //when
        Double totalTime = ReportUtil.calculateUserTimeInProject(project1, user1, TimePeriod.CURRENT_WEEK);

        //then
        assertThat(totalTime).isEqualTo(0);
    }
}
