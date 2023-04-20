package fr.openobservatory.backend.entities;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @Column(columnDefinition = "VARCHAR(64)", nullable = false, unique = true)
  private String name;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String image;

  @Column(columnDefinition = "INTEGER", nullable = false)
  private Integer validityTime;

  // ---

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "celestialBody")
  private Set<ObservationEntity> observations;

  // ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CelestialBodyEntity that = (CelestialBodyEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
