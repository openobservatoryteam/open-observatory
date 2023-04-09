package fr.openobservatory.backend.services;

import static org.assertj.core.api.Assertions.*;

import fr.openobservatory.backend.dto.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.UpdateCelestialBodyDto;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert;
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

  @Spy private ModelMapper modelMapper;
  @Mock private CelestialBodyRepository celestialBodyRepository;

  @InjectMocks private CelestialBodyService celestialBodyService;

  @DisplayName("CelestialBodyService#create should return registered CelestialBody with ")
  @Test
  void create_should_return_celestialBody_with_valid_input() {
    // Given
    var id = 1L;
    var dto = new CreateCelestialBodyDto("Neptune", 3, "image");
    // When
    Mockito.when(celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(dto.getName()))
        .thenReturn(false);
    Mockito.when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .thenAnswer(
            answer -> {
              var entity = answer.getArgument(0, CelestialBodyEntity.class);
              entity.setId(id);
              return entity;
            });
    var celestialBody = celestialBodyService.create(dto);
    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(dto.getName());
    assertThat(celestialBody.getImage()).isEqualTo(dto.getImage());
    assertThat(celestialBody.getValidityTime()).isEqualTo(dto.getValidityTime());
  }

  @DisplayName("CelestialBodyService#create should return registered CelestialBody with ")
  @Test
  void create_should_fail_with_existing_celestialBody_name() {
    // Given
    var dto = new CreateCelestialBodyDto("Neptune", 3, "image");
    // When
    Mockito.when(celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(dto.getName()))
        .thenReturn(true);
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.create(dto);
    // Then
    assertThatThrownBy(action).isInstanceOf(CelestialBodyNameAlreadyUsedException.class);
  }

  @DisplayName("CelestialBodyService#create should fail with too short name")
  @Test
  void create_should_fail_with_too_short_name() {
    // Given
    var dto = new CreateCelestialBodyDto("a", 5, "image");
    // When
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.create(dto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidCelestialBodyNameException.class);
  }

  @DisplayName("CelestialBodyService#create should fail with too long name")
  @Test
  void create_should_fail_with_too_long_name() {
    // Given
    var dto =
        new CreateCelestialBodyDto(
            "Ceci est un nom d'objet celeste qui n'est pas valid car il fait plus de 64 caractères",
            5,
            "image");
    // When
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.create(dto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidCelestialBodyNameException.class);
  }

  @DisplayName("CelestialBodyService#create should fail with too low validity time")
  @Test
  void create_should_fail_with_too_low_validity_time() {
    // Given
    var dto = new CreateCelestialBodyDto("Neptune", 0, "image");
    // When
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.create(dto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidCelestialBodyValidityTimeException.class);
  }

  @DisplayName("CeletialBodyService#create should faiil with too high validity time")
  @Test
  void create_should_fail_with_too_high_validity_time() {
    // Given
    var dto = new CreateCelestialBodyDto("Neptune", 13, "image");
    // When
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.create(dto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidCelestialBodyValidityTimeException.class);
  }

  @DisplayName("CelestialBodyService#findById should return found celestial body with given id")
  @Test
  void findById_should_return_celestialBody_with_given_id() {
    // Given
    var id = 1L;
    var name = "Neptune";
    var image = "image";
    var validityTime = 5;
    // When
    Mockito.when(celestialBodyRepository.findById(Mockito.isA(Long.class)))
        .thenAnswer(
            answer -> {
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

  @DisplayName("CelestialBodyService#findById should return nothing with unknown id")
  @Test
  void findById_should_return_nothing_with_unknown_id() {
    // Given
    var id = 5L;
    // When
    Mockito.when(celestialBodyRepository.findById(id)).thenReturn(Optional.empty());
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.findById(id);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownCelestialBodyException.class);
  }

  @DisplayName("CelestialBodyService#search should return page with celestial bodies")
  @Test
  void search_should_return_page_with_celestial_bodies() {
    // Given
    var page = 1;
    var itemsPerPage = 10;
    var id = 1L;
    var name = "Neptune";
    var image = "image";
    var validityTime = 5;
    var pageable = PageRequest.of(page, itemsPerPage);
    // When
    Mockito.when(celestialBodyRepository.findAll(Mockito.isA(PageRequest.class)))
        .thenAnswer(
            answer -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setValidityTime(validityTime);
              entity.setImage(image);
              var list = List.of(entity);
              return new PageImpl<>(list, pageable, 1);
            });
    var searchDto = celestialBodyService.search(page, itemsPerPage);
    // Then
    assertThat(searchDto.getData()).isNotEmpty();
    assertThat(searchDto.getData().get(0).getId()).isEqualTo(id);
    assertThat(searchDto.getData().get(0).getName()).isEqualTo(name);
    assertThat(searchDto.getData().get(0).getValidityTime()).isEqualTo(validityTime);
    assertThat(searchDto.getData().get(0).getImage()).isEqualTo(image);
  }

  @DisplayName("CelestialBodyService#search should fail with to low items per page")
  @Test
  void search_should_fail_with_to_low_items_per_page() {
    // Given
    var page = 1;
    var itemsPerPage = -1;
    // When
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.search(page, itemsPerPage);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidPaginationException.class);
  }

  @DisplayName("CelestialBodyService#search should fail with to high items per page")
  @Test
  void search_should_fail_with_to_high_items_per_page() {
    // Given
    var page = 1;
    var itemsPerPage = 200;
    // When
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.search(page, itemsPerPage);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidPaginationException.class);
  }

  @DisplayName("CelestialBodyService#search should fail with below zero page number")
  @Test
  void search_should_fail_with_below_zero_page_number() {
    // Given
    var page = -1;
    var itemsPerPage = 5;
    // When
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.search(page, itemsPerPage);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidPaginationException.class);
  }

  @DisplayName("CelestialBodyService#update should return updated celestial body")
  @Test
  void update_should_return_updated_celestial_body() {
    // Given
    var id = 3L;
    var name = "Neptune";
    var validityTime = 5;
    var image = "image";
    var newName = JsonNullable.of("Mars");
    var newValidityTime = JsonNullable.of(6);
    var newImage = JsonNullable.of("new image");
    var updateDto = new UpdateCelestialBodyDto();
    updateDto.setName(newName);
    updateDto.setValidityTime(newValidityTime);
    updateDto.setImage(newImage);
    // When
    Mockito.when(celestialBodyRepository.findById(Mockito.isA(Long.class)))
        .thenAnswer(
            answer -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setImage(image);
              entity.setValidityTime(validityTime);
              return Optional.of(entity);
            });
    Mockito.when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0, CelestialBodyEntity.class));
    var celestialBody = celestialBodyService.update(id, updateDto);
    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(newName.get());
    assertThat(celestialBody.getValidityTime()).isEqualTo(newValidityTime.get());
    assertThat(celestialBody.getImage()).isEqualTo(newImage.get());
  }

  @DisplayName("CelestialBodyService#update should return updated celestial body with no name")
  @Test
  void update_should_return_updated_celestial_body_with_no_name() {
    // Given
    var id = 3L;
    var name = "Neptune";
    var validityTime = 5;
    var image = "image";
    JsonNullable<String> newName = JsonNullable.undefined();
    var newValidityTime = JsonNullable.of(6);
    var newImage = JsonNullable.of("new image");
    var updateDto = new UpdateCelestialBodyDto();
    updateDto.setName(newName);
    updateDto.setValidityTime(newValidityTime);
    updateDto.setImage(newImage);
    // When
    Mockito.when(celestialBodyRepository.findById(Mockito.isA(Long.class)))
        .thenAnswer(
            answer -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setImage(image);
              entity.setValidityTime(validityTime);
              return Optional.of(entity);
            });
    Mockito.when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0, CelestialBodyEntity.class));
    var celestialBody = celestialBodyService.update(id, updateDto);
    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(name);
    assertThat(celestialBody.getValidityTime()).isEqualTo(newValidityTime.get());
    assertThat(celestialBody.getImage()).isEqualTo(newImage.get());
  }

  @DisplayName(
      "CelestialBodyService#update should return updated celestial body with no validity time")
  @Test
  void update_should_return_updated_celestial_body_with_no_validityTime() {
    // Given
    var id = 3L;
    var name = "Neptune";
    var validityTime = 5;
    var image = "image";
    var newName = JsonNullable.of("Mars");
    JsonNullable<Integer> newValidityTime = JsonNullable.undefined();
    var newImage = JsonNullable.of("new image");
    var updateDto = new UpdateCelestialBodyDto();
    updateDto.setName(newName);
    updateDto.setValidityTime(newValidityTime);
    updateDto.setImage(newImage);
    // When
    Mockito.when(celestialBodyRepository.findById(Mockito.isA(Long.class)))
        .thenAnswer(
            answer -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setImage(image);
              entity.setValidityTime(validityTime);
              return Optional.of(entity);
            });
    Mockito.when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0, CelestialBodyEntity.class));
    var celestialBody = celestialBodyService.update(id, updateDto);
    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(newName.get());
    assertThat(celestialBody.getValidityTime()).isEqualTo(validityTime);
    assertThat(celestialBody.getImage()).isEqualTo(newImage.get());
  }

  @DisplayName("CelestialBodyService#update should return updated celestial body with no image")
  @Test
  void update_should_return_updated_celestial_body_with_no_image() {
    // Given
    var id = 3L;
    var name = "Neptune";
    var validityTime = 5;
    var image = "image";
    var newName = JsonNullable.of("Mars");
    var newValidityTime = JsonNullable.of(6);
    JsonNullable<String> newImage = JsonNullable.undefined();
    var updateDto = new UpdateCelestialBodyDto();
    updateDto.setName(newName);
    updateDto.setValidityTime(newValidityTime);
    updateDto.setImage(newImage);
    // When
    Mockito.when(celestialBodyRepository.findById(Mockito.isA(Long.class)))
        .thenAnswer(
            answer -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setImage(image);
              entity.setValidityTime(validityTime);
              return Optional.of(entity);
            });
    Mockito.when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0, CelestialBodyEntity.class));
    var celestialBody = celestialBodyService.update(id, updateDto);
    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(newName.get());
    assertThat(celestialBody.getValidityTime()).isEqualTo(newValidityTime.get());
    assertThat(celestialBody.getImage()).isEqualTo(image);
  }

  @DisplayName(
      "CelestialBodyService#update should return updated celestial body new name is same as name")
  @Test
  void update_should_return_updated_celestial_body_when_newName_same_as_name() {
    // Given
    var id = 3L;
    var name = "Neptune";
    var validityTime = 5;
    var image = "image";
    var newName = JsonNullable.of("Neptune");
    var newValidityTime = JsonNullable.of(6);
    var newImage = JsonNullable.of("new image");
    var updateDto = new UpdateCelestialBodyDto();
    updateDto.setName(newName);
    updateDto.setValidityTime(newValidityTime);
    updateDto.setImage(newImage);
    // When
    Mockito.when(celestialBodyRepository.findById(Mockito.isA(Long.class)))
        .thenAnswer(
            answer -> {
              var entity = new CelestialBodyEntity();
              entity.setId(id);
              entity.setName(name);
              entity.setImage(image);
              entity.setValidityTime(validityTime);
              return Optional.of(entity);
            });
    Mockito.when(celestialBodyRepository.save(Mockito.isA(CelestialBodyEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0, CelestialBodyEntity.class));
    var celestialBody = celestialBodyService.update(id, updateDto);
    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(newName.get());
    assertThat(celestialBody.getValidityTime()).isEqualTo(newValidityTime.get());
    assertThat(celestialBody.getImage()).isEqualTo(newImage.get());
  }

  @DisplayName("CelestialBodyService#update should fail with unknown celestial body")
  @Test
  void update_should_fail_with_unknown_celestialBody() {
    // Given
    var id = 2L;
    // When
    Mockito.when(celestialBodyRepository.findById(id))
        .thenThrow(UnknownCelestialBodyException.class);
    ThrowableAssert.ThrowingCallable action =
        () -> celestialBodyService.update(id, new UpdateCelestialBodyDto());
    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownCelestialBodyException.class);
  }

  @DisplayName("CelestialBodyService#update should fail with too short celestiial body name")
  @Test
  void update_should_fail_with_too_short_celestialBody_name() {
    // Given
    var id = 2L;
    var updateDto = new UpdateCelestialBodyDto();
    var newName = JsonNullable.of("a");
    updateDto.setName(newName);
    // When
    Mockito.when(celestialBodyRepository.findById(id))
        .thenReturn(Optional.of(new CelestialBodyEntity()));
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.update(id, updateDto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidCelestialBodyNameException.class);
  }

  @DisplayName("CelestialBodyService#update should fail with too long celestiial body name")
  @Test
  void update_should_fail_with_too_long_celestialBody_name() {
    // Given
    var id = 2L;
    var updateDto = new UpdateCelestialBodyDto();
    var newName =
        JsonNullable.of(
            "Ceci est un nom d'objet celeste qui n'est pas valid car il fait plus de 64 caractères");
    updateDto.setName(newName);
    // When
    Mockito.when(celestialBodyRepository.findById(id))
        .thenReturn(Optional.of(new CelestialBodyEntity()));
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.update(id, updateDto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidCelestialBodyNameException.class);
  }

  @DisplayName("CelestialBodyService#update should fail with already used celestial body name")
  @Test
  void update_should_fail_with_already_used_celestialBody_name() {
    // Given
    var id = 2L;
    var name = "Mars";
    var updateDto = new UpdateCelestialBodyDto();
    var newName = JsonNullable.of("Neptune");
    updateDto.setName(newName);
    // When
    Mockito.when(celestialBodyRepository.findById(id))
        .thenAnswer(
            answer -> {
              var entity = new CelestialBodyEntity();
              entity.setName(name);
              return Optional.of(entity);
            });
    Mockito.when(celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(newName.get()))
        .thenReturn(true);
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.update(id, updateDto);
    // Then
    assertThatThrownBy(action).isInstanceOf(CelestialBodyNameAlreadyUsedException.class);
  }

  @DisplayName("CelestialBodyService#update should fail with too low validity time")
  @Test
  void update_should_fail_with_too_low_validity_time() {
    // Given
    var id = 2L;
    var updateDto = new UpdateCelestialBodyDto();
    var newValidityTime = JsonNullable.of(0);
    updateDto.setValidityTime(newValidityTime);
    // When
    Mockito.when(celestialBodyRepository.findById(id))
        .thenReturn(Optional.of(new CelestialBodyEntity()));
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.update(id, updateDto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidCelestialBodyValidityTimeException.class);
  }

  @DisplayName("CelestialBodyService#update should fail with too high validity time")
  @Test
  void update_should_fail_with_too_high_validity_time() {
    // Given
    var id = 2L;
    var updateDto = new UpdateCelestialBodyDto();
    var newValidityTime = JsonNullable.of(13);
    updateDto.setValidityTime(newValidityTime);
    // When
    Mockito.when(celestialBodyRepository.findById(id))
        .thenReturn(Optional.of(new CelestialBodyEntity()));
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.update(id, updateDto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidCelestialBodyValidityTimeException.class);
  }

  @DisplayName("CelestialBodyService#delete should fail with unknown celestial body")
  @Test
  void delete_should_fail_with_unknown_celestialBody() {
    // Given
    var id = 3L;
    // When
    Mockito.when(celestialBodyRepository.existsById(id)).thenReturn(false);
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.delete(id);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownCelestialBodyException.class);
  }

  @DisplayName("CelestialBodyService#delete should return nothing with existing celestial body")
  @Test
  void delete_should_return_nothing_with_existing_celestialBody() {
    // Given
    var id = 2L;
    // When
    Mockito.when(celestialBodyRepository.existsById(id)).thenReturn(true);
    ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.delete(id);
    // Then
    assertThatNoException().isThrownBy(action);
  }
}
