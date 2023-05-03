package fr.openobservatory.backend.dto.input;

import fr.openobservatory.backend.validation.FileSize;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
public class SendMediaDto {

  @NotNull(message = "file.required")
  @FileSize(message = "file.size", max = 3_000_000L)
  private MultipartFile file;
}
