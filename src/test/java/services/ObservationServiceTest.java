package services;

import static org.assertj.core.api.Assertions.*;

import fr.openobservatory.backend.dto.CreateObservationDto;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import fr.openobservatory.backend.repositories.ObservationRepository;
import fr.openobservatory.backend.repositories.ObservationVoteRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import fr.openobservatory.backend.services.ObservationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
public class ObservationServiceTest {
    @Mock private CelestialBodyRepository celestialBodyRepository;
    @Spy private ModelMapper modelMapper = new ModelMapper();
    @Mock private ObservationRepository observationRepository;
    @Mock private ObservationVoteRepository observationVoteRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ObservationService observationService;

    @BeforeEach
    void setup_modelmapper() {
        modelMapper.addConverter(
                new Converter<Instant, OffsetDateTime>() {
                    @Override
                    public OffsetDateTime convert(MappingContext<Instant, OffsetDateTime> context) {
                        return context.getSource().atOffset(ZoneOffset.UTC);
                    }
                });
    }

    @DisplayName("ObservationService#should return registered observation")
    @Test
    void create_should_return_registered_observation() {
        //Given
        var username = "Keke27210";
        var celestialBodyId = 2L;
        var desc = "La lune";
        var lng = 49.3;
        var lat = 12.5;
        var orientation = 30;
        var visibility = ObservationEntity.Visibility.VISIBLE;
        var timestamp = OffsetDateTime.of(2023, 3,21,18,12,30,0, ZoneOffset.UTC);
        var createDto = new CreateObservationDto(celestialBodyId, desc, lng, lat, orientation, visibility, timestamp);
        //When
        Mockito.when(userRepository.findByUsernameIgnoreCase(username))
                .thenAnswer(answer -> {
                    var entity = new UserEntity();
                    entity.setUsername(username);
                    return Optional.of(entity);
                });
        Mockito.when(celestialBodyRepository.findById(celestialBodyId))
                .thenAnswer(answer -> {
                    var entity = new CelestialBodyEntity();
                    entity.setId(celestialBodyId);
                    return Optional.of(entity);
                });
        Mockito.when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
                .thenAnswer(answer -> answer.getArgument(0));
        var observation = observationService.create(username, createDto);
        //Then
        assertThat(observation.getAuthor().getUsername()).isEqualTo(username);
        assertThat(observation.getCelestialBody().getId()).isEqualTo(celestialBodyId);
        assertThat(observation.getDescription()).isEqualTo(desc);
        assertThat(observation.getLongitude()).isEqualTo(lng);
        assertThat(observation.getLatitude()).isEqualTo(lat);
        assertThat(observation.getOrientation()).isEqualTo(orientation);
        assertThat(observation.getVisibility()).isEqualTo(visibility);
        assertThat(observation.getCreatedAt()).isEqualTo(timestamp);
        assertThat(observation.getCurrentVote()).isNull();
        assertThat(observation.isExpired()).isFalse();
        assertThat(observation.getKarma()).isZero();
    }
}
