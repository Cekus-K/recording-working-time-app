package pl.cekus.antologicproject.utills;

import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.model.WorkingTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class ReportUtil {

    private ReportUtil() {
    }

    public static BigDecimal calculateUserCostInProject(Project project, User user, TimePeriod period) {
        return user.getCostPerHour().multiply(BigDecimal.valueOf(calculateUserTimeInProject(project, user, period)));
    }

    public static Double calculateUserTimeInProject(Project project, User user, TimePeriod period) {
        return round(user.getWorkingTimes().stream()
                .filter(workingTime -> workingTime.getProject().equals(project))
                .filter(workingTime -> filterWorkingTimesByPeriod(workingTime, period))
                .map(workingTime -> Duration.between(workingTime.getStartTime(), workingTime.getEndTime()).toSeconds())
                .mapToDouble(Long::longValue).sum() / 3600, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.CEILING);
        return bd.doubleValue();
    }

    private static boolean filterWorkingTimesByPeriod(WorkingTime workingTime, TimePeriod period) {
        switch (period) {
            case WEEK:
                // TODO: checking if it's the same week
                return ChronoUnit.WEEKS.between(workingTime.getStartTime(), LocalDateTime.now()) == 0;
            case MONTH:
                return workingTime.getStartTime().getMonthValue() == LocalDateTime.now().getMonthValue();
            case YEAR:
                return workingTime.getStartTime().getYear() == LocalDateTime.now().getYear();
            case ALL:
                return true;
            default:
                throw new IllegalArgumentException("invalid time period type");
        }
    }

    public enum TimePeriod {
        WEEK, MONTH, YEAR, ALL;
    }
}
