package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;
import lombok.*;
import lombok.Builder.Default;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "observation")
public class ObservationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 500)
  private String description;

  @Column(nullable = false)
  private Double latitude;

  @Column(nullable = false)
  private Double longitude;

  @Column(nullable = false)
  private Integer orientation;

  @ManyToOne(optional = false)
  private CelestialBodyEntity celestialBody;

  @ManyToOne(optional = false)
  private UserEntity author;

  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private Visibility visibility;

  @Column(nullable = false, updatable = false)
  private Instant timestamp;

  // ---

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "observation")
  @Default
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<ObservationVoteEntity> votes = Set.of();

  // ---

  public enum Visibility {
    CLEARLY_VISIBLE,
    VISIBLE,
    SLIGHTLY_VISIBLE,
    BARELY_VISIBLE
  }
}
