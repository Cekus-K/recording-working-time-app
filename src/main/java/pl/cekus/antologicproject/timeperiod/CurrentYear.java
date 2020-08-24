package pl.cekus.antologicproject.timeperiod;

import pl.cekus.antologicproject.model.WorkingTime;

import java.time.LocalDateTime;

class CurrentYear implements TypeOfPeriod {

    @Override
    public boolean isCorrectPeriod(WorkingTime workingTime) {
        return workingTime.getStartTime().getYear() == LocalDateTime.now().getYear();
    }
}
