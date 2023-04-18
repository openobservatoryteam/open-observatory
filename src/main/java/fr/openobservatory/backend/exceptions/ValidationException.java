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
    super("One or more errors were encountered while validating the supplied payload.");
    this.violations =
        Objects.requireNonNull(violations, "violations must not be null").stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());
  }
}
