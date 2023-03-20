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
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ObservationService {

  private final CelestialBodyRepository celestialBodyRepository;
  private final ModelMapper modelMapper;
  private final ObservationRepository observationRepository;
  private final ObservationVoteRepository observationVoteRepository;
  private final UserRepository userRepository;

  // ---

  public ObservationWithDetailsDto create(String issuerUsername, CreateObservationDto dto) {
    if (dto.getDescription().length() > 2048) throw new InvalidObservationDescriptionException();
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

  public List<ObservationDto> findAllNearby(Double lng, Double lat) {
    return observationRepository.findAll().stream()
        .filter(
            o ->
                calculateDistanceBetweenTwoPoints(lng, lat, o.getLongitude(), o.getLatitude())
                    <= 30)
        .map(o -> modelMapper.map(o, ObservationDto.class))
        .toList();
  }

  public List<ObservationDto> search(Integer limit, Integer page) {
    return observationRepository.findAll().stream()
        .limit(limit)
        .map(o -> modelMapper.map(o, ObservationDto.class))
        .toList();
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
    observationVoteRepository.save(vote);
  }

  public ObservationWithDetailsDto update(
      Long id, UpdateObservationDto dto, String issuerUsername) {
    var issuer =
        userRepository
            .findByUsernameIgnoreCase(issuerUsername)
            .orElseThrow(UnavailableUserException::new);
    var observation =
        observationRepository.findById(id).orElseThrow(InvalidCelestialBodyIdException::new);
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

  // Calculate distance between two points using the "haversine" formula
  private double calculateDistanceBetweenTwoPoints(
      double lon1, double lat1, double lon2, double lat2) {
    int earthRadius = 6371;

    double phi1 = lat1 * Math.PI / 180.0d;
    double phi2 = lat2 * Math.PI / 180.0d;
    double deltaPhi = (lat2 - lat1) * Math.PI / 180.0d;
    double deltaLambda = (lon2 - lon1) * Math.PI / 180.0d;

    // square of half the chord length between the points
    double a =
        Math.sin(deltaPhi / 2.0d) * Math.sin(deltaPhi / 2.0d)
            + Math.cos(phi1)
                * Math.cos(phi2)
                * Math.sin(deltaLambda / 2.0d)
                * Math.sin(deltaLambda / 2.0d);

    // angular distance in radians
    double c = 2.0d * Math.atan2(Math.sqrt(a), Math.sqrt(1.0d - a));

    return earthRadius * c;
  }

  private boolean isEditableBy(ObservationEntity observation, UserEntity issuer) {
    return issuer.getType().equals(UserEntity.Type.ADMIN) || observation.getAuthor().equals(issuer);
  }
}
