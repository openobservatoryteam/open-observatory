package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

import lombok.*;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Entity
@Table(name = "push_subscription")
public class PushSubscriptionEntity {

  @Id
  @Column(columnDefinition = "VARCHAR(240)", nullable = false, updatable = false)
  private String endpoint;

  @Column(columnDefinition = "CHAR(24)", nullable = false)
  private String auth;

  @Column(columnDefinition = "CHAR(88)", nullable = false)
  private String p256dh;

  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
  private Instant createdAt;

  // ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PushSubscriptionEntity that = (PushSubscriptionEntity) o;
    return Objects.equals(endpoint, that.endpoint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(endpoint);
  }
}
