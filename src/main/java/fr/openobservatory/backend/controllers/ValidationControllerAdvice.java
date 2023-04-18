package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationControllerAdvice {

  @ExceptionHandler(ValidationException.class)
  public ProblemDetail validationException(ValidationException exception) {
    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    problem.setTitle("VALIDATION_ERROR");
    problem.setProperty("violations", exception.getViolations());
    return problem;
  }
}
