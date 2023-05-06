package fr.openobservatory.backend.validation;

import fr.openobservatory.backend.validation.validator.EnumValueConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValueConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumValue {

  Class<? extends Enum<?>> value();

  String message() default "{fr.openobservatory.backend.validation.EnumValue.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
