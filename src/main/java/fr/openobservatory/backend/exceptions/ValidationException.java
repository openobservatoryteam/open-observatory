package fr.openobservatory.backend.exceptions;

import jakarta.validation.ConstraintViolation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

  private final Set<String> violations;

  public <T> ValidationException(Set<ConstraintViolation<T>> violations) {
    this.violations =
        Objects.requireNonNull(violations).stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());
  }
}
