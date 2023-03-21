package fr.openobservatory.backend.entities;

public interface CelestialBody {
  Long getId();

  String getName();

  String getImage();

  Integer getValidityTime();
}
