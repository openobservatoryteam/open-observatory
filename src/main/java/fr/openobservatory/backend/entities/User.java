package fr.openobservatory.backend.entities;

import java.time.Instant;

public interface User {
  Long getId();

  String getUsername();

  String getPassword();

  String getAvatar();

  String getBiography();

  boolean isPublic();

  UserEntity.Type getType();

  Instant getCreatedAt();
}
