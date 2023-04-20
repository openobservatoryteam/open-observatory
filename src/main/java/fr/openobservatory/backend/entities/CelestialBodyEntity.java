package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.Set;

import lombok.*;
import lombok.Builder.Default;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "celestial_body")
public class CelestialBodyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 64, nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  @Lob
  private String image;

  @Column(nullable = false)
  private Integer validityTime;

  // ---

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "celestialBody")
  @Default
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<ObservationEntity> observations = Set.of();
}
