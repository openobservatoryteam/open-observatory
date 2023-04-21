package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.input.*;
import fr.openobservatory.backend.dto.output.ObservationDto;
import fr.openobservatory.backend.dto.output.ObservationWithDetailsDto;
import fr.openobservatory.backend.dto.output.SearchResultsDto;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import fr.openobservatory.backend.repositories.ObservationRepository;
import fr.openobservatory.backend.repositories.ObservationVoteRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import java.time.Instant;
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

  private final AchievementService achievementService;
  private final CelestialBodyRepository celestialBodyRepository;
  private final ModelMapper modelMapper;
  private final ObservationRepository observationRepository;
  private final ObservationVoteRepository observationVoteRepository;
  private final PushSubscriptionService pushSubscriptionService;
  private final UserRepository userRepository;
  private final Validator validator;

  // ---

  @Transactional
  public ObservationWithDetailsDto create(String issuerUsername, CreateObservationDto dto) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty()) throw new ValidationException(violations);
    var issuer = findIssuer(issuerUsername, false);
    var celestialBody =
        celestialBodyRepository
            .findById(dto.getCelestialBodyId())
            .orElseThrow(InvalidCelestialBodyIdException::new);
    var observation = modelMapper.map(dto, ObservationEntity.class);
    observation.setAuthor(issuer);
    observation.setCelestialBody(celestialBody);
    var savedObservation = observationRepository.save(observation);
    achievementService.checkForAchievements(savedObservation);
    // Quite ugly, to be optimized
    var notifiableUsers =
        userRepository
            .findAllByNotificationEnabledIsTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndPositionAtIsGreaterThanEqual(
                Instant.now().minus(7, ChronoUnit.DAYS));
    var notification =
        PushNotificationDto.builder()
            .code("OBSERVATION_NEARBY")
            .link("/observations/" + savedObservation.getId())
            .build();
    notifiableUsers.forEach(
        user -> {
          if (user.equals(issuer)) return;
          double[] topLeft =
              getPointCorner(
                  user.getLatitude(), user.getLongitude(), -user.getNotificationRadius());
          double[] bottomRight =
              getPointCorner(user.getLatitude(), user.getLongitude(), user.getNotificationRadius());
          if (topLeft[0] < observation.getLatitude()
              && observation.getLatitude() < bottomRight[0]
              && topLeft[1] < observation.getLongitude()
              && observation.getLongitude() < bottomRight[1]) {
            pushSubscriptionService.sendTo(user.getUsername(), notification);
          }
        });
    return buildDetailed(savedObservation, issuer);
  }

  public ObservationWithDetailsDto findById(Long id, String issuerUsername) {
    var issuer = findIssuer(issuerUsername, true);
    var observation =
        observationRepository.findById(id).orElseThrow(UnknownObservationException::new);
    return buildDetailed(observation, issuer);
  }

  public List<ObservationDto> findAllNearby(FindNearbyObservationsDto dto) {
    double distance = Math.max(0, Math.min(dto.getRadius(), MAX_NEARBY_DISTANCE));
    double[] topLeft = getPointCorner(dto.getLatitude(), dto.getLongitude(), -distance);
    double[] bottomRight = getPointCorner(dto.getLatitude(), dto.getLongitude(), distance);
    return observationRepository
        .findAllNearby(topLeft[0], bottomRight[0], topLeft[1], bottomRight[1])
        .stream()
        .map(o -> modelMapper.map(o, ObservationDto.class))
        .toList();
  }

  public SearchResultsDto<ObservationWithDetailsDto> search(
      PaginationDto dto, String issuerUsername) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty()) throw new ValidationException(violations);
    var issuer = findIssuer(issuerUsername, false);
    return SearchResultsDto.from(
        observationRepository
            .findAllByOrderByTimestampDesc(
                Pageable.ofSize(dto.getItemsPerPage()).withPage(dto.getPage()))
            .map(o -> buildDetailed(o, issuer)));
  }

  public void submitVote(Long observationId, SubmitVoteDto dto, String issuerUsername) {
    var issuer = findIssuer(issuerUsername, false);
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
    var violations = validator.validate(dto);
    if (!violations.isEmpty()) throw new ValidationException(violations);
    var issuer = findIssuer(issuerUsername, false);
    var observation =
        observationRepository.findById(id).orElseThrow(UnknownObservationException::new);
    if (!isEditableBy(observation, issuer)) throw new ObservationNotEditableException();
    if (dto.getDescription().isPresent()) {
      observation.setDescription(dto.getDescription().get());
    }
    return buildDetailed(observationRepository.save(observation), issuer);
  }

  public ObservationDto delete(Long id) {
    var obs = observationRepository.findById(id).orElseThrow(UnknownObservationException::new);
    observationRepository.deleteById(id);
    return modelMapper.map(obs, ObservationDto.class);
  }

  // ---

  private ObservationWithDetailsDto buildDetailed(
      ObservationEntity observation, UserEntity issuer) {
    var dto = modelMapper.map(observation, ObservationWithDetailsDto.class);
    dto.setCurrentVote(
        observation.getVotes().stream()
            .filter(v -> v.getUser().equals(issuer))
            .map(ObservationVoteEntity::getVote)
            .findFirst()
            .orElse(null));
    dto.setExpired(
        observation
            .getTimestamp()
            .plus(observation.getCelestialBody().getValidityTime(), ChronoUnit.HOURS)
            .isBefore(Instant.now()));
    dto.setKarma(
        observation.getVotes().stream()
            .map(vote -> vote.getVote().getWeight())
            .reduce(0, Integer::sum));
    return dto;
  }

  /**
   * Calculates coordinates given a point and a distance.
   *
   * @param lat Latitude to start from.
   * @param lng Longitude to start from.
   * @param distance Distance (in kilometers) to shift of.
   * @return An array containing {shiftedLatitude, shiftedLongitude}.
   * @implNote Involved formulas: <a href="https://stackoverflow.com/a/1253545"></a>
   */
  private double[] getPointCorner(double lat, double lng, double distance) {
    double latShift = distance / RATIO_KM_LATITUDE;
    double lngShift = distance / (RATIO_KM_LONGITUDE * Math.cos(Math.toRadians(lat)));
    return new double[] {lat + latShift, lng + lngShift};
  }

  private UserEntity findIssuer(String issuerUsername, boolean allowGuest) {
    if (allowGuest && issuerUsername == null) return null;
    return userRepository
        .findByUsernameIgnoreCase(issuerUsername)
        .orElseThrow(UnavailableUserException::new);
  }

  private boolean isEditableBy(ObservationEntity observation, UserEntity issuer) {
    return issuer.getType().equals(UserEntity.Type.ADMIN) || observation.getAuthor().equals(issuer);
  }
}
