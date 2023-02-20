package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.ObservationDto;
import fr.openobservatory.backend.repositories.ObservationRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ObservationService {

  private final ModelMapper modelMapper;
  private final ObservationRepository observationRepository;

  // ---

  public List<ObservationDto> search(Integer limit, Integer page) {
    return observationRepository.findAll().stream()
        .limit(limit)
        .map(o -> modelMapper.map(o, ObservationDto.class))
        .toList();
  }

  public List<ObservationDto> findNearbyObservations(Double lng, Double lat) {
    return observationRepository.findAll().stream()
        .filter(
            o ->
                calculateDistanceBetweenTwoPoints(lng, lat, o.getLongitude(), o.getLatitude())
                    <= 30)
        .map(o -> modelMapper.map(o, ObservationDto.class))
        .toList();
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
