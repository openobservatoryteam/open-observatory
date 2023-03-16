package fr.openobservatory.backend.entities;

import java.time.Instant;

public interface Observation {
  Long getId();

  String getDescription();

  Double getLatitude();

  Double getLongitude();

  Integer getOrientation();

  CelestialBodyEntity getCelestialBody();

  UserEntity getAuthor();

  ObservationEntity.Visibility getVisibility();

  Instant getCreatedAt();

  boolean hasExpired();
}
