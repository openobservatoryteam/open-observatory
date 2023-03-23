package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.ISSPositionDto;
import fr.openobservatory.backend.exceptions.UnavailableISSPositionsException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@EnableScheduling
@Service
public class ISSService {

  private static final String API_URL =
      "https://api.wheretheiss.at/v1/satellites/{1}/positions?timestamps={2}&units={3}";
  private static final String CACHING_KEY = "iss-positions";
  private static final int CACHING_TTL = 30 * 1000;
  private static final int NORAD_ISS_ID = 25544;
  private static final List<Integer> INSTANT_DELTAS =
      List.of(-40, -30, -20, -10, 0, 10, 20, 30, 40, 50);
  private static final String UNITS = "kilometers";

  private RestTemplate restTemplate;

  // ---

  /**
   * Computes positions of the ISS around the time the request is done. For a request done at a time
   * H, positions from H-9 to H+10 will be returned. A margin of 5 minutes between the returned
   * "current" timestamp and the actual timestamp is to be expected due to caching mechanisms.
   */
  @Cacheable(CACHING_KEY)
  public List<ISSPositionDto> findISSPositions() {
    var now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    var timestamps =
        INSTANT_DELTAS.stream()
            .map(d -> String.valueOf(now.plus(d, ChronoUnit.MINUTES).getEpochSecond()))
            .collect(Collectors.joining(","));
    var response =
        restTemplate.getForEntity(API_URL, ISSPositionDto[].class, NORAD_ISS_ID, timestamps, UNITS);
    if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null)
      throw new UnavailableISSPositionsException();
    var positions = Arrays.asList(response.getBody());
    positions.stream()
        .filter(p -> p.getTimestamp().toInstant().equals(now))
        .forEach(p -> p.setCurrent(true));
    return positions;
  }

  @CacheEvict(allEntries = true, value = CACHING_KEY)
  @Scheduled(fixedDelay = CACHING_TTL)
  public void clearISSPositionsCache() {}
}
