package fr.openobservatory.backend.services;

import fr.openobservatory.backend.entities.Observation;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.exceptions.UnknownUserException;
import fr.openobservatory.backend.repositories.ObservationRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ObservationService {

  private final ModelMapper modelMapper;
  private final ObservationRepository observationRepository;
  private final UserService userService;

  // ---

  public Optional<? extends Observation> findById(Long id) {
    return observationRepository.findById(id);
  }

  public List<? extends Observation> search(Integer limit, Integer page) {
    return observationRepository.findAll().stream().limit(limit).toList();
  }

  public List<Observation> findNearbyObservations(Double lng, Double lat) {
    return observationRepository.findAll().stream()
        .filter(
            o ->
                calculateDistanceBetweenTwoPoints(lng, lat, o.getLongitude(), o.getLatitude())
                    <= 30)
        .map(o -> modelMapper.map(o, Observation.class))
        .toList();
  }

  public Collection<? extends Observation> findObservationsByAuthor(String username) {
    var user =
        (UserEntity) userService.findByUsername(username).orElseThrow(UnknownUserException::new);
    return observationRepository.findAllByAuthor(user, Pageable.ofSize(100)).stream().toList();
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
