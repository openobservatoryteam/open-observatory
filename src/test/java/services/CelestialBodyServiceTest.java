package services;

import static org.assertj.core.api.Assertions.*;

import fr.openobservatory.backend.dto.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.UpdateCelestialBodyDto;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.exceptions.InvalidCelestialBodyNameException;
import fr.openobservatory.backend.exceptions.InvalidCelestialBodyValidityTimeException;
import fr.openobservatory.backend.exceptions.InvalidPaginationException;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import fr.openobservatory.backend.services.CelestialBodyService;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CelestialBodyServiceTest {

    @Mock private CelestialBodyRepository celestialBodyRepository;

    @InjectMocks private CelestialBodyService celestialBodyService;

    @DisplayName("CelestialBodyService#create should return registered CelestialBody with ")
    @Test
    void create_should_return_celestialBody_with_valid_input() {
        // Given
        var id = 1L;
        var dto = new CreateCelestialBodyDto("Neptune", 3, "image");
        // When
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
                            return Optional.of(entity);
                        });
        var celestialBody = celestialBodyService.findById(id);
        // Then
        assertThat(celestialBody).isPresent();
        assertThat(celestialBody.get().getId()).isEqualTo(id);
        assertThat(celestialBody.get().getName()).isEqualTo(name);
        assertThat(celestialBody.get().getImage()).isEqualTo(image);
        assertThat(celestialBody.get().getValidityTime()).isEqualTo(validityTime);
    }

    @DisplayName("CelestialBodyService#findById should return nothing with unknow id")
    @Test
    void findById_should_return_nothing_with_unknow_id() {
        // Given
        var id = 5L;
        // When
        Mockito.when(celestialBodyRepository.findById(id)).thenReturn(Optional.empty());
        var celestialBody = celestialBodyService.findById(id);
        // Then
        assertThat(celestialBody).isNotPresent();
    }

    @DisplayName("CelestialBodyService#search should return page with celestial bodies")
    @Test
    void search_should_return_page_with_celestial_bodies() {
        // Given
        var page = 1;
        var itemsPerPage = 10;
        var pageable = PageRequest.of(page, itemsPerPage);
        // When
        Mockito.when(celestialBodyRepository.findAll(Mockito.isA(PageRequest.class)))
                .thenAnswer(answer -> Page.empty(pageable));
    }

    @DisplayName("CelestialBodyService#search should fail with to low items per page")
    @Test
    void search_should_fail_with_to_low_items_per_page() {
        // Given
        var page = 1;
        var itemsPerPage = -1;
        var pageable = PageRequest.of(page, itemsPerPage);
        // When
        Mockito.when(celestialBodyRepository.findAll(pageable)).thenReturn(Page.empty());
        ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.search(page, itemsPerPage);
        // Then
        assertThatThrownBy(action).isInstanceOf(InvalidPaginationException.class);
    }

    @DisplayName("CelestialBodyService#search should fail with to high items per page")
    @Test
    void search_should_fail_with_to_high_items_per_page() {
        // Given
        var page = 1;
        var itemsPerPage = 12;
        var pageable = PageRequest.of(page, itemsPerPage);
        // When
        Mockito.when(celestialBodyRepository.findAll(pageable)).thenReturn(Page.empty());
        ThrowableAssert.ThrowingCallable action = () -> celestialBodyService.search(page, itemsPerPage);
        // Then
        assertThatThrownBy(action).isInstanceOf(InvalidPaginationException.class);
    }

    @DisplayName("CelestialBodyService#search should fail with below zero page number")
    @Test
    void search_should_fail_with_to_low__items_per_page() {
        // Given
        var page = -1;
        var itemsPerPage = 5;
        var pageable = PageRequest.of(page, itemsPerPage);
        // When
        Mockito.when(celestialBodyRepository.findAll(pageable)).thenReturn(Page.empty());
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
}
