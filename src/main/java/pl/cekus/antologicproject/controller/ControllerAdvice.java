package pl.cekus.antologicproject.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.cekus.antologicproject.exception.IllegalParameterException;
import pl.cekus.antologicproject.exception.NotFoundException;
import pl.cekus.antologicproject.exception.NotUniqueException;

@RestControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(NotUniqueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleNotUniqueException(NotUniqueException e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    @ExceptionHandler(IllegalParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleIllegalUserRoleException(IllegalParameterException e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String handleNotFoundException(NotFoundException e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }
}
