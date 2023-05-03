package fr.openobservatory.backend.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import fr.openobservatory.backend.dto.input.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.input.PaginationDto;
import fr.openobservatory.backend.dto.input.UpdateCelestialBodyDto;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CelestialBodyServiceTest {

  @Spy ModelMapper modelMapper;
  @Mock CelestialBodyRepository celestialBodyRepository;
  @Spy Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  @InjectMocks CelestialBodyService celestialBodyService;

  // --- CelestialBodyService#create

  @DisplayName("CelestialBodyService#create should create a celestial body")
  @Test
  void create_should_create_a_celestial_body() {
    // Given
    var dto =
        CreateCelestialBodyDto.builder().name("Neptune").validityTime(3).image("image").build();

    // When
    when(celestialBodyRepository.save(isA(CelestialBodyEntity.class))).then(a -> a.getArgument(0));
    var celestialBody = celestialBodyService.create(dto);

    // Then
    assertThat(celestialBody.getName()).isEqualTo(dto.getName());
    assertThat(celestialBody.getValidityTime()).isEqualTo(dto.getValidityTime());
    assertThat(celestialBody.getImage()).isEqualTo(dto.getImage());
  }

  @DisplayName("CelestialBodyService#create should throw when dto is invalid")
  @Test
  void create_should_throw_when_dto_is_invalid() {
    // Given
    var dto = CreateCelestialBodyDto.builder().name("a").validityTime(5).image("image").build();

    // When
    ThrowingCallable action = () -> celestialBodyService.create(dto);

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("violations", Set.of("name.size"));
  }

  @DisplayName("CelestialBodyService#create should throw when name is already used")
  @Test
  void create_should_throw_when_name_is_already_used() {
    // Given
    var dto =
        CreateCelestialBodyDto.builder().name("Neptune").validityTime(3).image("image").build();

    // When
    when(celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(dto.getName()))
        .thenReturn(true);
    ThrowingCallable action = () -> celestialBodyService.create(dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(CelestialBodyNameAlreadyUsedException.class);
  }

  // --- CelestialBodyService#delete

  @DisplayName("CelestialBodyService#delete should delete the targeted body")
  @Test
  void delete_should_delete_the_targeted_body() {
    // Given
    var id = 2L;

    // When
    when(celestialBodyRepository.existsById(id)).thenReturn(true);
    celestialBodyService.delete(id);

    // Then
    verify(celestialBodyRepository, times(1)).deleteById(id);
  }

  @DisplayName("CelestialBodyService#delete should throw when body is unknown")
  @Test
  void delete_should_throw_when_body_is_unknown() {
    // Given
    var id = 3L;

    // When
    ThrowingCallable action = () -> celestialBodyService.delete(id);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownCelestialBodyException.class);
  }

  // --- CelestialBodyService#findById

  @DisplayName("CelestialBodyService#findById should return found celestial body with given id")
  @Test
  void findById_should_return_celestialBody_with_given_id() {
    // Given
    var id = 1L;
    var name = "Neptune";
    var image = "image";
    var validityTime = 5;
    // When
    when(celestialBodyRepository.findById(isA(Long.class)))
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
    when(celestialBodyRepository.findById(id)).thenReturn(Optional.empty());
    ThrowingCallable action = () -> celestialBodyService.findById(id);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownCelestialBodyException.class);
  }

  // --- CelestialBodyService#search

  @DisplayName("CelestialBodyService#search should search celestial bodies")
  @Test
  void search_should_search_celestial_bodies() {
    // Given
    var dto = PaginationDto.builder().itemsPerPage(1).page(0).build();
    var entity =
        CelestialBodyEntity.builder()
            .id(33L)
            .name("Neptune")
            .validityTime(5)
            .image("image")
            .build();

    // When
    when(celestialBodyRepository.findAll(isA(Pageable.class)))
        .thenReturn(
            new PageImpl<>(
                List.of(entity),
                Pageable.ofSize(dto.getItemsPerPage()).withPage(dto.getPage()),
                1));
    var search = celestialBodyService.search(dto);

    // Then
    assertThat(search.getItemsPerPage()).isEqualTo(dto.getItemsPerPage());
    assertThat(search.getPage()).isEqualTo(dto.getPage());
    assertThat(search.getItemCount()).isOne();
    assertThat(search.getData()).isNotEmpty();

    var body = search.getData().get(0);
    assertThat(body.getId()).isEqualTo(entity.getId());
    assertThat(body.getName()).isEqualTo(entity.getName());
    assertThat(body.getValidityTime()).isEqualTo(entity.getValidityTime());
    assertThat(body.getImage()).isEqualTo(entity.getImage());
  }

  @DisplayName("CelestialBodyService#search should throw when dto is invalid")
  @Test
  void search_should_fail_with_to_low_items_per_page() {
    // Given
    var dto = PaginationDto.builder().itemsPerPage(-3).page(-1).build();

    // When
    ThrowingCallable action = () -> celestialBodyService.search(dto);

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("violations", Set.of("itemsPerPage.range", "page.range"));
  }

  // --- CelestialBodyService#update

  @DisplayName("CelestialBodyService#update should update the targeted body (name != newName)")
  @Test
  void update_should_update_the_targeted_body_name_neq_newName() {
    // Given
    var id = 3L;
    var entity =
        CelestialBodyEntity.builder().id(id).name("Neptune").validityTime(5).image("image").build();
    var dto =
        UpdateCelestialBodyDto.builder()
            .name(JsonNullable.of("Mars"))
            .validityTime(JsonNullable.of(8))
            .image(JsonNullable.of("new_image"))
            .build();

    // When
    when(celestialBodyRepository.findById(id)).thenReturn(Optional.of(entity));
    when(celestialBodyRepository.save(isA(CelestialBodyEntity.class)))
        .thenAnswer(a -> a.getArgument(0));
    var celestialBody = celestialBodyService.update(id, dto);

    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(dto.getName().get());
    assertThat(celestialBody.getValidityTime()).isEqualTo(dto.getValidityTime().get());
    assertThat(celestialBody.getImage()).isEqualTo(dto.getImage().get());
  }

  @DisplayName("CelestialBodyService#update should update the targeted body (name == newName)")
  @Test
  void update_should_update_the_targeted_body_name_eq_newName() {
    // Given
    var id = 3L;
    var entity =
        CelestialBodyEntity.builder().id(id).name("Neptune").validityTime(5).image("image").build();
    var dto =
        UpdateCelestialBodyDto.builder()
            .name(JsonNullable.of("Neptune"))
            .validityTime(JsonNullable.of(8))
            .image(JsonNullable.of("new_image"))
            .build();

    // When
    when(celestialBodyRepository.findById(id)).thenReturn(Optional.of(entity));
    when(celestialBodyRepository.save(isA(CelestialBodyEntity.class)))
        .thenAnswer(a -> a.getArgument(0));
    var celestialBody = celestialBodyService.update(id, dto);

    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(dto.getName().get());
    assertThat(celestialBody.getValidityTime()).isEqualTo(dto.getValidityTime().get());
    assertThat(celestialBody.getImage()).isEqualTo(dto.getImage().get());
  }

  @DisplayName("CelestialBodyService#update should leave the body as is")
  @Test
  void update_should_leave_the_body_as_is() {
    // Given
    var id = 3L;
    var entity =
        CelestialBodyEntity.builder().id(id).name("Neptune").validityTime(5).image("image").build();
    var dto =
        UpdateCelestialBodyDto.builder()
            .name(JsonNullable.undefined())
            .validityTime(JsonNullable.undefined())
            .image(JsonNullable.undefined())
            .build();

    // When
    when(celestialBodyRepository.findById(id)).thenReturn(Optional.of(entity));
    when(celestialBodyRepository.save(isA(CelestialBodyEntity.class)))
        .thenAnswer(a -> a.getArgument(0));
    var celestialBody = celestialBodyService.update(id, dto);

    // Then
    assertThat(celestialBody.getId()).isEqualTo(id);
    assertThat(celestialBody.getName()).isEqualTo(entity.getName());
    assertThat(celestialBody.getValidityTime()).isEqualTo(entity.getValidityTime());
    assertThat(celestialBody.getImage()).isEqualTo(entity.getImage());
  }

  @DisplayName("CelestialBodyService#update should throw when dto is invalid")
  @Test
  void update_should_throw_when_dto_is_invalid() {
    // Given
    var id = 3L;
    var dto =
        UpdateCelestialBodyDto.builder()
            .name(JsonNullable.of("CB"))
            .validityTime(JsonNullable.of(900))
            .image(JsonNullable.undefined())
            .build();

    // When
    ThrowingCallable action = () -> celestialBodyService.update(id, dto);

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("violations", Set.of("name.size", "validityTime.range"));
  }

  @DisplayName("CelestialBodyService#update should throw when new name is already used")
  @Test
  void update_should_throw_when_new_name_is_already_used() {
    // Given
    var id = 3L;
    var entity =
        CelestialBodyEntity.builder().id(id).name("Neptune").validityTime(5).image("image").build();
    var dto =
        UpdateCelestialBodyDto.builder()
            .name(JsonNullable.of("Mars"))
            .validityTime(JsonNullable.of(8))
            .image(JsonNullable.of("new_image"))
            .build();

    // When
    when(celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(dto.getName().get()))
        .thenReturn(true);
    when(celestialBodyRepository.findById(id)).thenReturn(Optional.of(entity));
    ThrowingCallable action = () -> celestialBodyService.update(id, dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(CelestialBodyNameAlreadyUsedException.class);
  }
}
