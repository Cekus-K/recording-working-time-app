package pl.cekus.antologicproject.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.cekus.antologicproject.dto.WorkingTimeDto;
import pl.cekus.antologicproject.form.WorkingTimeCreateForm;
import pl.cekus.antologicproject.service.WorkingTimeService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
class WorkingTimeController extends ResponseEntityExceptionHandler {

    private final WorkingTimeService workingTimeService;

    WorkingTimeController(WorkingTimeService workingTimeService) {
        this.workingTimeService = workingTimeService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/working-times")
    void addWorkingTime(@RequestBody WorkingTimeCreateForm workingTimeCreateForm) {
        workingTimeService.addWorkingTimeToUser(workingTimeCreateForm);
    }

    @GetMapping("/working-times")
    List<WorkingTimeDto> readWorkingTimes(@RequestParam(name = "employee") String employee) {
        return workingTimeService.readAllWorkingTimes(employee);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/working-times/{id}")
    void updateWorkingTime(@PathVariable Long id, @RequestBody @Valid WorkingTimeCreateForm workingTime) {
        workingTimeService.updateWorkingTime(id, workingTime);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/working-times/{id}")
    void deleteWorkingTime(@PathVariable Long id) {
        workingTimeService.deleteWorkingTime(id);
    }
}
