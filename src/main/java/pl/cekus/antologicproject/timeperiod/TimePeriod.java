package pl.cekus.antologicproject.timeperiod;

import pl.cekus.antologicproject.model.WorkingTime;

public enum TimePeriod {

    CURRENT_YEAR(new CurrentYear()),
    CURRENT_MONTH(new CurrentMonth()),
    CURRENT_WEEK(new CurrentWeek());

    private TypeOfPeriod typeOfPeriod;

    TimePeriod(TypeOfPeriod typeOfPeriod) {
        this.typeOfPeriod = typeOfPeriod;
    }

    public static boolean isCorrectPeriod(TimePeriod timePeriod, WorkingTime workingTime) {
        return timePeriod.typeOfPeriod.isCorrectPeriod(workingTime);
    }
}
