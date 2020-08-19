package pl.cekus.antologicproject.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.cekus.antologicproject.dto.WorkingTimeDto;
import pl.cekus.antologicproject.form.WorkingTimeCreateForm;
import pl.cekus.antologicproject.service.WorkingTimeService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

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

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/working-times/{uuid}")
    void updateWorkingTime(@PathVariable UUID uuid, @RequestBody @Valid WorkingTimeCreateForm workingTime) {
        workingTimeService.updateWorkingTime(uuid, workingTime);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/working-times/{uuid}")
    void deleteWorkingTime(@PathVariable UUID uuid) {
        workingTimeService.deleteWorkingTime(uuid);
    }
}
