package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.dto.VoteDto;
import fr.openobservatory.backend.entities.Observation;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import fr.openobservatory.backend.repositories.ObservationRepository;
import fr.openobservatory.backend.repositories.ObservationVoteRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ObservationService {

  private final CelestialBodyRepository celestialBodyRepository;
  private final ObservationRepository observationRepository;
  private final ObservationVoteRepository observationVoteRepository;
  private final UserRepository userRepository;

  // ---

  public Optional<? extends Observation> findById(Long id) {
    return observationRepository.findById(id);
  }

  public List<? extends Observation> search(Integer limit, Integer page) {
    return observationRepository.findAll().stream().limit(limit).toList();
  }

  public Observation update(Long id, UpdateObservationDto dto, String issuer) {
    ObservationEntity observation =
        observationRepository.findById(id).orElseThrow(UnknownObservationException::new);
    if (!observation.getAuthor().getUsername().equalsIgnoreCase(issuer))
      throw new ObservationNotEditableException();
    if (dto.getDescription().isPresent()) {
      var description = dto.getDescription().get();
      if (description.length() > 2048) throw new InvalidObservationDescriptionException();
      observation.setDescription(description);
    }
    return observationRepository.save(observation);
  }

  public List<? extends Observation> findNearbyObservations(Double lng, Double lat) {
    return observationRepository.findAll().stream()
        .filter(
            o ->
                calculateDistanceBetweenTwoPoints(lng, lat, o.getLongitude(), o.getLatitude())
                    <= 30)
        .toList();
  }

  public Collection<? extends Observation> findObservationsByAuthor(String username) {
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    return observationRepository.findAllByAuthor(user, Pageable.ofSize(100)).stream().toList();
  }

  public void voteObservation(Long observationId, String username, VoteDto dto) {
    var observation =
        observationRepository.findById(observationId).orElseThrow(UnknownObservationException::new);
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    var currentVote = observationVoteRepository.findByObservationAndUser(observation, user);
    if (dto.getVote() == null) {
      if (currentVote.isEmpty()) return;
      observationVoteRepository.delete(currentVote.get());
      return;
    }
    var vote = currentVote.orElse(new ObservationVoteEntity());
    vote.setUser(user);
    vote.setObservation(observation);
    vote.setVote(dto.getVote());
    observationVoteRepository.save(vote);
  }

  public Observation createObservation(String username, CreateObservationDto dto) {
    var celestialBody =
        celestialBodyRepository
            .findById(dto.getCelestialBodyId())
            .orElseThrow(UnknownCelestialBodyException::new);
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    ObservationEntity observation = new ObservationEntity();
    observation.setCreatedAt(dto.getTimestamp().toInstant());
    observation.setVisibility(dto.getVisibility());
    observation.setDescription(dto.getDescription());
    observation.setLongitude(dto.getLng());
    observation.setLatitude(dto.getLat());
    observation.setOrientation(dto.getOrientation());
    observation.setCelestialBody(celestialBody);
    observation.setAuthor(user);
    return observationRepository.save(observation);
  }

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
}
