package fr.openobservatory.backend.entities;

import lombok.Data;

@Data
public class NotificationEntity {

  private String auth;

  private String endpoint;

  private String p256dh;
}
