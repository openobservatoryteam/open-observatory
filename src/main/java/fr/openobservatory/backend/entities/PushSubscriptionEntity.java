package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "push_subscription")
public class PushSubscriptionEntity {

  @Id
  @Column(length = 240, nullable = false, updatable = false)
  private String endpoint;

  @Column(length = 24, nullable = false)
  private String auth;

  @Column(length = 88, nullable = false)
  private String p256dh;

  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;
}
