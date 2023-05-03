package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.input.SendMediaDto;
import fr.openobservatory.backend.entities.MediaEntity;
import fr.openobservatory.backend.exceptions.ValidationException;
import fr.openobservatory.backend.repositories.MediaRepository;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MediaService {

  private final MediaRepository mediaRepository;
  private final Validator validator;

  // ---

  public MediaEntity create(SendMediaDto dto) throws IOException {
    var violations = validator.validate(dto);
    if (!violations.isEmpty()) throw new ValidationException(violations);
    var media = MediaEntity.builder().data(dto.getFile().getBytes()).build();
    return mediaRepository.save(media);
  }

  public void delete(Long id) {
    mediaRepository.deleteById(id);
  }

  public Optional<MediaEntity> findById(Long id) {
    return mediaRepository.findById(id);
  }
}
