package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import lombok.Data;

@Data
@Entity
@Table(name = "observation")
public class ObservationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  @Lob
  private String description;

  @Column(columnDefinition = "FLOAT", nullable = false)
  private Double latitude;

  @Column(columnDefinition = "FLOAT", nullable = false)
  private Double longitude;

  @Column(columnDefinition = "INTEGER", nullable = false)
  private Integer orientation;

  @ManyToOne
  @JoinColumn(name = "celestial_body_id")
  private CelestialBodyEntity celestialBody;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private UserEntity author;

  @Column(columnDefinition = "TINYINT", nullable = false)
  private Visibility visibility;

  @Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
  private Instant createdAt;

  // ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ObservationEntity that = (ObservationEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  // ---

  public enum Visibility {
    CLEARLY_VISIBLE,
    VISIBLE,
    SLIGHTLY_VISIBLE,
    BARELY_VISIBLE
  }
}
