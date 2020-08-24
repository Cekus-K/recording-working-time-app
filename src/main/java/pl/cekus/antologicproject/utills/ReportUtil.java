package pl.cekus.antologicproject.utills;

import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.timeperiod.TimePeriod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public final class ReportUtil {

    private ReportUtil() {
    }

    public static BigDecimal calculateUserCostInProject(Project project, User user, TimePeriod period) {
        return user.getCostPerHour().multiply(BigDecimal.valueOf(calculateUserTimeInProject(project, user, period)));
    }

    public static Double calculateUserTimeInProject(Project project, User user, TimePeriod period) {
        return roundToTwoDecimalPlaces(project.getWorkingTimes().stream()
                .filter(workingTime -> workingTime.getUser().equals(user))
                .filter(workingTime -> period == null || TimePeriod.isCorrectPeriod(period, workingTime))
                .map(workingTime -> Duration.between(workingTime.getStartTime(), workingTime.getEndTime()).toSeconds())
                .mapToDouble(Long::longValue).sum() / 3600);
    }

    private static double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.CEILING);
        return bd.doubleValue();
    }
}
