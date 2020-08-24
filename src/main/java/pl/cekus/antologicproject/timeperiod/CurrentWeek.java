package pl.cekus.antologicproject.timeperiod;

import pl.cekus.antologicproject.model.WorkingTime;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

class CurrentWeek implements TypeOfPeriod {

    @Override
    public boolean isCorrectPeriod(WorkingTime workingTime) {
        return workingTime.getStartTime().toLocalDate().get(ChronoField.ALIGNED_WEEK_OF_YEAR) ==
                LocalDateTime.now().toLocalDate().get(ChronoField.ALIGNED_WEEK_OF_YEAR) &&
                workingTime.getStartTime().getYear() == LocalDateTime.now().getYear();
    }
}
