package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.exceptions.ValidationException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@AllArgsConstructor
@RestControllerAdvice
@Hidden
public class ValidationController {

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ProblemDetail> validationException(ValidationException exception) {
    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    problem.setTitle("VALIDATION_ERROR");
    problem.setProperty("violations", exception.getViolations());
    return ResponseEntity.badRequest().body(problem);
  }
}
