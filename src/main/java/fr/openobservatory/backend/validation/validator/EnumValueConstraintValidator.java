package fr.openobservatory.backend.validation.validator;

import fr.openobservatory.backend.validation.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValueConstraintValidator implements ConstraintValidator<EnumValue, String> {

  private Class<? extends Enum<?>> clazz;

  // ---

  @Override
  public void initialize(EnumValue constraintAnnotation) {
    this.clazz = constraintAnnotation.value();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;
    return Arrays.stream(clazz.getEnumConstants()).anyMatch(c -> c.name().equals(value));
  }
}
