package pl.cekus.antologicproject.timeperiod;

import pl.cekus.antologicproject.model.WorkingTime;

import java.time.LocalDateTime;

class CurrentMonth implements TypeOfPeriod {

    @Override
    public boolean isCorrectPeriod(WorkingTime workingTime) {
        return workingTime.getStartTime().getMonth() == LocalDateTime.now().getMonth();
    }
}
