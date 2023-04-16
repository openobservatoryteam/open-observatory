package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.dto.VoteDto;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import fr.openobservatory.backend.repositories.ObservationRepository;
import fr.openobservatory.backend.repositories.ObservationVoteRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ObservationService {

  private static final double RATIO_KM_LATITUDE = 110.574;
  private static final double RATIO_KM_LONGITUDE = 111.320;
  private static final double MAX_NEARBY_DISTANCE = 250;

  private final CelestialBodyRepository celestialBodyRepository;
  private final ModelMapper modelMapper;
  private final ObservationRepository observationRepository;
  private final ObservationVoteRepository observationVoteRepository;
  private final PushSubscriptionService pushSubscriptionService;
  private final UserRepository userRepository;
  private final AchievementService achievementService;

  // ---

  public ObservationWithDetailsDto create(String issuerUsername, CreateObservationDto dto) {
    if (dto.getDescription() != null && dto.getDescription().length() > 2048)
      throw new InvalidObservationDescriptionException();
    if (dto.getTimestamp().isAfter(OffsetDateTime.now()))
      throw new InvalidObservationCreationTimeException();
    var issuer =
        userRepository
            .findByUsernameIgnoreCase(issuerUsername)
            .orElseThrow(UnavailableUserException::new);
    var celestialBody =
        celestialBodyRepository
            .findById(dto.getCelestialBodyId())
            .orElseThrow(InvalidCelestialBodyIdException::new);
    var observation = new ObservationEntity();
    observation.setAuthor(issuer);
    observation.setDescription(dto.getDescription());
    observation.setCelestialBody(celestialBody);
    observation.setLongitude(dto.getLng());
    observation.setLatitude(dto.getLat());
    observation.setOrientation(dto.getOrientation());
    observation.setVisibility(dto.getVisibility());
    observation.setCreatedAt(dto.getTimestamp().toInstant());
    var observationDto =
        modelMapper.map(observationRepository.save(observation), ObservationWithDetailsDto.class);
    observationDto.setCurrentVote(null);
    observationDto.setExpired(false);
    observationDto.setKarma(0);
    achievementService.checkForAchievements(observation);
    // Quite ugly, to be optimized
    var notifiableUsers =
        userRepository
            .findAllByNotificationsEnabledIsTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndLastPositionUpdateIsGreaterThanEqual(
                Instant.now().minus(7, ChronoUnit.DAYS));
    var notification =
        new PushNotificationDto()
            .setCode("OBSERVATION_NEARBY")
            .setLink("/observations/" + observationDto.getId());
    notifiableUsers.forEach(
        user -> {
          if (user.equals(issuer)) return;
          double[] topLeft =
              getPointCorner(user.getLatitude(), user.getLongitude(), -user.getRadius());
          double[] bottomRight =
              getPointCorner(user.getLatitude(), user.getLongitude(), user.getRadius());
          if (topLeft[0] < observation.getLatitude()
              && observation.getLatitude() < bottomRight[0]
              && topLeft[1] < observation.getLongitude()
              && observation.getLongitude() < bottomRight[1]) {
            pushSubscriptionService.sendTo(user.getUsername(), notification);
          }
        });
    return observationDto;
  }

  public ObservationWithDetailsDto findById(Long id, String issuerUsername) {
    var issuer =
        issuerUsername != null
            ? userRepository
                .findByUsernameIgnoreCase(issuerUsername)
                .orElseThrow(UnavailableUserException::new)
            : null;
    var observation =
        observationRepository.findById(id).orElseThrow(UnknownObservationException::new);
    var votes = observationVoteRepository.findAllByObservation(observation);
    var observationDto = modelMapper.map(observation, ObservationWithDetailsDto.class);
    observationDto.setCurrentVote(
        issuer != null
            ? observationVoteRepository
                .findByObservationAndUser(observation, issuer)
                .map(ObservationVoteEntity::getVote)
                .orElse(null)
            : null);
    observationDto.setExpired(
        observation
            .getCreatedAt()
            .plus(observation.getCelestialBody().getValidityTime(), ChronoUnit.HOURS)
            .isBefore(Instant.now()));
    observationDto.setKarma(
        votes.stream().map(vote -> vote.getVote().getWeight()).reduce(0, Integer::sum));
    return observationDto;
  }

  public List<ObservationDto> findAllNearby(Double lng, Double lat, Double radius) {
    double distance = Math.max(0, Math.min(radius, MAX_NEARBY_DISTANCE));
    double[] topLeft = getPointCorner(lat, lng, -distance);
    double[] bottomRight = getPointCorner(lat, lng, distance);
    return observationRepository
        .findAllNearby(topLeft[0], bottomRight[0], topLeft[1], bottomRight[1])
        .stream()
        .map(o -> modelMapper.map(o, ObservationDto.class))
        .toList();
  }

  public SearchResultsDto<ObservationWithDetailsDto> search(Integer page, Integer itemsPerPage) {
    if (itemsPerPage < 0 || itemsPerPage > 100 || page < 0) throw new InvalidPaginationException();
    return SearchResultsDto.from(
        observationRepository
            .findAllByOrderByCreatedAtDesc(Pageable.ofSize(itemsPerPage))
            .map(o -> modelMapper.map(o, ObservationWithDetailsDto.class)));
  }

  public void submitVote(Long observationId, VoteDto dto, String issuerUsername) {
    var issuer =
        userRepository
            .findByUsernameIgnoreCase(issuerUsername)
            .orElseThrow(UnavailableUserException::new);
    var observation =
        observationRepository.findById(observationId).orElseThrow(UnknownObservationException::new);
    var currentVote = observationVoteRepository.findByObservationAndUser(observation, issuer);
    if (dto.getVote() == null) {
      if (currentVote.isEmpty()) return;
      observationVoteRepository.delete(currentVote.get());
      return;
    }
    var vote = currentVote.orElse(new ObservationVoteEntity());
    vote.setUser(issuer);
    vote.setObservation(observation);
    vote.setVote(dto.getVote());
    var saveVote = observationVoteRepository.save(vote);
    achievementService.checkForAchievements(saveVote);
  }

  public ObservationWithDetailsDto update(
      Long id, UpdateObservationDto dto, String issuerUsername) {
    var issuer =
        userRepository
            .findByUsernameIgnoreCase(issuerUsername)
            .orElseThrow(UnavailableUserException::new);
    var observation =
        observationRepository.findById(id).orElseThrow(UnknownObservationException::new);
    if (!isEditableBy(observation, issuer)) throw new ObservationNotEditableException();
    if (dto.getDescription().isPresent()) {
      var description = dto.getDescription().get();
      if (description.length() > 2048) throw new InvalidObservationDescriptionException();
      observation.setDescription(description);
    }
    return modelMapper.map(
        observationRepository.save(observation), ObservationWithDetailsDto.class);
  }

  // ---

  /**
   * Calculates coordinates given a point and a distance.
   *
   * @param lat Latitude to start from.
   * @param lng Longitude to start from.
   * @param distance Distance (in kilometers) to shift of.
   * @return An array containing {shiftedLatitude, shiftedLongitude}.
   * @implNote Involved formulas: https://stackoverflow.com/a/1253545
   */
  private double[] getPointCorner(double lat, double lng, double distance) {
    double latShift = distance / RATIO_KM_LATITUDE;
    double lngShift = distance / (RATIO_KM_LONGITUDE * Math.cos(Math.toRadians(lat)));
    return new double[] {lat + latShift, lng + lngShift};
  }

  private boolean isEditableBy(ObservationEntity observation, UserEntity issuer) {
    return issuer.getType().equals(UserEntity.Type.ADMIN) || observation.getAuthor().equals(issuer);
  }
}
