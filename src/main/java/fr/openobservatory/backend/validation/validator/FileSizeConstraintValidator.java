package fr.openobservatory.backend.validation.validator;

import fr.openobservatory.backend.validation.FileSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeConstraintValidator implements ConstraintValidator<FileSize, MultipartFile> {

  private long min;
  private long max;

  // ---

  @Override
  public void initialize(FileSize constraintAnnotation) {
    this.min = constraintAnnotation.min();
    this.max = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
    if (value == null) return true;
    return min <= value.getSize() && value.getSize() <= max;
  }
}
