package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import lombok.Data;

@Data
@Entity
@Table(name = "observation")
public class ObservationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
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

  @Column(columnDefinition = "SMALLINT", nullable = false)
  private Visibility visibility;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  // ---

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "observation")
  private Set<ObservationVoteEntity> votes;

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
