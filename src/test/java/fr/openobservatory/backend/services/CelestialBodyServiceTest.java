package fr.openobservatory.backend.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.openobservatory.backend.dto.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.SearchDto;
import fr.openobservatory.backend.dto.UpdateCelestialBodyDto;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CelestialBodyServiceTest {

  @Mock private CelestialBodyRepository celestialBodyRepository;

  @Spy private ModelMapper modelMapper;

  @Spy
  Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @InjectMocks private CelestialBodyService celestialBodyService;

  // --- CelestialBodyService#create

  @DisplayName("CelestialBodyService#create should create a celestial body")
  @Test
  void create_should_create_a_celestial_body() {
    // Given
    var dto = new CreateCelestialBodyDto("Neptune", 3, "image");

    // When
    when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .then(a -> a.getArgument(0));
    var celestialBody = celestialBodyService.create(dto);

    // Then
    assertThat(celestialBody.getName()).isEqualTo(dto.getName());
    assertThat(celestialBody.getImage()).isEqualTo(dto.getImage());
    assertThat(celestialBody.getValidityTime()).isEqualTo(dto.getValidityTime());
  }

  @DisplayName("CelestialBodyService#create should throw when name is already used")
  @Test
  void create_should_throw_when_name_is_already_used() {
    // Given
    var dto = new CreateCelestialBodyDto("Neptune", 3, "image");

    // When
    when(celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(dto.getName()))
        .thenReturn(true);
    ThrowingCallable action = () -> celestialBodyService.create(dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(CelestialBodyNameAlreadyUsedException.class);
  }

  @DisplayName("CelestialBodyService#create should throw when dto is invalid")
  @Test
  void create_should_throw_when_dto_is_invalid() {
    // Given
    var dto = new CreateCelestialBodyDto("a", -3, "image");

    // When
    ThrowingCallable action = () -> celestialBodyService.create(dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(ValidationException.class);
  }

  // --- CelestialBodyService#findById

  @DisplayName("CelestialBodyService#findById should return a celestial body")
  @Test
  void findById_should_return_a_celestial_body() {
    // Given
    var id = 1L;
    var name = "Neptune";
    var image = "image";
    var validityTime = 5;

    // When
    when(celestialBodyRepository.findById(id))
        .thenAnswer(
            a -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setValidityTime(validityTime);
              entity.setImage(image);
              return Optional.of(entity);
            });
    var celestialBody = celestialBodyService.findById(id);

    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(name);
    assertThat(celestialBody.getValidityTime()).isEqualTo(validityTime);
    assertThat(celestialBody.getImage()).isEqualTo(image);
  }

  @DisplayName("CelestialBodyService#findById should throw when id is unknown")
  @Test
  void findById_should_throw_when_id_is_unknown() {
    // Given
    var id = 5L;

    // When
    when(celestialBodyRepository.findById(id)).thenReturn(Optional.empty());
    ThrowingCallable action = () -> celestialBodyService.findById(id);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownCelestialBodyException.class);
  }

  // --- CelestialBodyService#search

  @DisplayName("CelestialBodyService#search should return celestial bodies")
  @Test
  void search_should_return_page_with_celestial_bodies() {
    // Given
    var id = 1L;
    var name = "Neptune";
    var image = "image";
    var validityTime = 5;
    var dto = new SearchDto(10, 1);

    // When
    when(celestialBodyRepository.findAll(Mockito.isA(PageRequest.class)))
        .thenAnswer(
            a -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setValidityTime(validityTime);
              entity.setImage(image);
              return new PageImpl<>(List.of(entity), PageRequest.of(dto.getPage(), dto.getItemsPerPage()), 1);
            });
    var searchDto = celestialBodyService.search(dto);

    // Then
    assertThat(searchDto.getData()).isNotEmpty();
    assertThat(searchDto.getData().get(0).getId()).isEqualTo(id);
    assertThat(searchDto.getData().get(0).getName()).isEqualTo(name);
    assertThat(searchDto.getData().get(0).getValidityTime()).isEqualTo(validityTime);
    assertThat(searchDto.getData().get(0).getImage()).isEqualTo(image);
  }

  @DisplayName("CelestialBodyService#search should throw when dto is invalid")
  @Test
  void search_should_fail_with_to_low_items_per_page() {
    // Given
    var dto = new SearchDto(420, -50);

    // When
    ThrowingCallable action = () -> celestialBodyService.search(dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(ValidationException.class);
  }

  // --- CelestialBodyService#update

  @DisplayName("CelestialBodyService#update should update a celestial body if names match")
  @Test
  void update_should_update_a_celestial_body_if_name_match() {
    // Given
    var id = 3L;
    var name = "Neptune";
    var validityTime = 5;
    var image = "base64:image";
    var dto = new UpdateCelestialBodyDto(JsonNullable.of(name), JsonNullable.of(validityTime), JsonNullable.of(image));

    // When
    when(celestialBodyRepository.findById(id))
        .then(
            a -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setImage(image);
              entity.setValidityTime(validityTime);
              return Optional.of(entity);
            });
    when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .thenAnswer(a -> a.getArgument(0));
    var celestialBody = celestialBodyService.update(id, dto);

    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(name);
    assertThat(celestialBody.getImage()).isEqualTo(image);
    assertThat(celestialBody.getValidityTime()).isEqualTo(validityTime);
  }

  @DisplayName("CelestialBodyService#update should update a celestial body if new name is free")
  @Test
  void update_should_update_a_celestial_body_if_new_name_is_free() {
    // Given
    var id = 3L;
    var name = "Neptune";
    var newName = "Mars";
    var validityTime = 5;
    var image = "base64:image";
    var dto = new UpdateCelestialBodyDto(JsonNullable.of(newName), JsonNullable.of(validityTime), JsonNullable.of(image));

    // When
    when(celestialBodyRepository.findById(id))
        .then(
            a -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setImage(image);
              entity.setValidityTime(validityTime);
              return Optional.of(entity);
            });
    when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .thenAnswer(a -> a.getArgument(0));
    var celestialBody = celestialBodyService.update(id, dto);

    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(newName);
    assertThat(celestialBody.getImage()).isEqualTo(image);
    assertThat(celestialBody.getValidityTime()).isEqualTo(validityTime);
  }

  @DisplayName("CelestialBodyService#update should update nothing")
  @Test
  void update_should_update_nothing() {
    // Given
    var id = 3L;
    var name = "Neptune";
    var validityTime = 5;
    var image = "base64:image";
    var dto = new UpdateCelestialBodyDto(JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined());

    // When
    when(celestialBodyRepository.findById(id))
        .then(
            a -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setImage(image);
              entity.setValidityTime(validityTime);
              return Optional.of(entity);
            });
    when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .thenAnswer(a -> a.getArgument(0));
    var celestialBody = celestialBodyService.update(id, dto);

    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(name);
    assertThat(celestialBody.getImage()).isEqualTo(image);
    assertThat(celestialBody.getValidityTime()).isEqualTo(validityTime);
  }

  @DisplayName("CelestialBodyService#update should when dto is invalid")
  @Test
  void update_should_throw_when_dto_is_invalid() {
    // Given
    var id = 2L;
    var dto = new UpdateCelestialBodyDto(JsonNullable.of("A"), JsonNullable.of(-5), JsonNullable.of(null));

    // When
    ThrowingCallable action = () -> celestialBodyService.update(id, dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(ValidationException.class);
  }

  @DisplayName("CelestialBodyService#update should throw when id is unknown")
  @Test
  void update_should_throw_when_id_is_unknown() {
    // Given
    var id = 2L;
    var dto = new UpdateCelestialBodyDto(JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined());

    // When
    when(celestialBodyRepository.findById(id)).thenReturn(Optional.empty());
    ThrowingCallable action = () -> celestialBodyService.update(id, dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownCelestialBodyException.class);
  }

  @DisplayName("CelestialBodyService#update should throw when new name is already used")
  @Test
  void update_should_throw_when_new_name_is_already_used() {
    // Given
    var id = 3L;
    var name = "Neptune";
    var validityTime = 5;
    var image = "base64:image";
    var dto = new UpdateCelestialBodyDto(JsonNullable.of("Mars"), JsonNullable.of(4), JsonNullable.of("base64:new_image"));

    // When
    when(celestialBodyRepository.findById(id))
        .then(
            a -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setImage(image);
              entity.setValidityTime(validityTime);
              return Optional.of(entity);
            });
    when(celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(dto.getName().get())).thenReturn(true);
    ThrowingCallable action = () -> celestialBodyService.update(id, dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(CelestialBodyNameAlreadyUsedException.class);
  }

  // --- CelestialBodyService#delete

  @DisplayName("CelestialBodyService#delete should delete a celestial body")
  @Test
  void delete_should_delete_a_celestial_body() {
    // Given
    var id = 3L;

    // When
    when(celestialBodyRepository.existsById(id)).thenReturn(true);
    celestialBodyService.delete(id);

    // Then
    verify(celestialBodyRepository, times(1)).deleteById(id);
  }

  @DisplayName("CelestialBodyService#delete should throw when id is unknown")
  @Test
  void delete_should_throw_when_id_is_unknown() {
    // Given
    var id = 2L;

    // When
    ThrowingCallable action = () -> celestialBodyService.delete(id);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownCelestialBodyException.class);
  }
}
